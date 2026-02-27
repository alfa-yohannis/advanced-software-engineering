#!/usr/bin/env python3
import argparse
import subprocess
import shutil
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict, List

try:
    from jinja2 import Environment, FileSystemLoader, StrictUndefined
except ModuleNotFoundError as exc:
    raise SystemExit(
        "Missing dependency: jinja2. Install with "
        "`python3 -m pip install -r mde/transformers/requirements.txt`."
    ) from exc

try:
    import yaml
except ModuleNotFoundError as exc:
    raise SystemExit(
        "Missing dependency: PyYAML. Install with "
        "`python3 -m pip install -r mde/transformers/requirements.txt`."
    ) from exc


@dataclass
class Paths:
    root: Path
    models: Path
    templates: Path


def load_yaml(path: Path) -> Dict[str, Any]:
    with path.open("r", encoding="utf-8") as f:
        data = yaml.safe_load(f)
    if not isinstance(data, dict):
        raise ValueError(f"YAML root must be a mapping: {path}")
    return data


def merge_dicts(base: Dict[str, Any], override: Dict[str, Any]) -> Dict[str, Any]:
    merged = dict(base)
    merged.update(override)
    return merged


def enrich_environment_context(defaults: Dict[str, Any], env_cfg: Dict[str, Any], output_root: Path) -> Dict[str, Any]:
    context = merge_dicts(defaults, env_cfg)

    name = str(context["name"])
    context["env_name"] = name

    # Derived defaults to keep environment model compact.
    context.setdefault("k8s_overlay", name)
    context.setdefault("env_value", name)
    context.setdefault("mp3_bucket", f"tts-{name}")
    context.setdefault("internal_token", f"changeme-{name}")
    context.setdefault("tfvars_filename", f"{name}.tfvars")
    context.setdefault("k8s_root_path", str((output_root / "kubernetes").resolve()))

    return context


def render_string(template_env: Environment, template_string: str, context: Dict[str, Any]) -> str:
    return template_env.from_string(template_string).render(**context)


def render_template(template_env: Environment, template_name: str, context: Dict[str, Any], destination: Path) -> None:
    content = template_env.get_template(template_name).render(**context)
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text(content, encoding="utf-8")


def should_render(record: Dict[str, Any], context: Dict[str, Any]) -> bool:
    condition = record.get("when")
    if not condition:
        return True
    return bool(context.get(condition, False))


def render_records(
    template_env: Environment,
    records: List[Dict[str, Any]],
    context: Dict[str, Any],
    output_root: Path,
) -> None:
    for record in records:
        if not should_render(record, context):
            continue
        output_rel = render_string(template_env, record["output"], context)
        render_template(template_env, record["template"], context, output_root / output_rel)


def clean_output_root(output_root: Path) -> None:
    # Keep generation idempotent while preserving unrelated files.
    for subdir in ["terraform", "kubernetes"]:
        target = output_root / subdir
        if target.exists():
            subprocess.run(["rm", "-rf", str(target)], check=True)


def generate(paths: Paths, output_root: Path) -> None:
    env_model = load_yaml(paths.models / "environments.yaml")
    file_model = load_yaml(paths.models / "files.yaml")

    defaults = env_model["defaults"]
    environments = env_model["environments"]
    core_files = file_model["core_files"]
    env_files = file_model["env_files"]

    template_env = Environment(
        loader=FileSystemLoader(str(paths.templates)),
        undefined=StrictUndefined,
        trim_blocks=True,
        lstrip_blocks=True,
    )

    output_root.mkdir(parents=True, exist_ok=True)
    clean_output_root(output_root)

    render_records(template_env, core_files, defaults, output_root)

    for env_cfg in environments:
        context = enrich_environment_context(defaults, env_cfg, output_root)

        for key in ["api_image_tag", "web_image_tag", "worker_image_tag", "tfvars_filename"]:
            context[key] = render_string(template_env, str(context[key]), context)

        render_records(template_env, env_files, context, output_root)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate IaC artifacts from MDE templates.")
    parser.add_argument(
        "--output-root",
        default="outputs",
        help="Output root directory (default: outputs)",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    repo_root = Path(__file__).resolve().parents[2]
    output_root = (repo_root / args.output_root).resolve()

    paths = Paths(
        root=repo_root,
        models=repo_root / "mde" / "models",
        templates=repo_root / "mde" / "templates",
    )

    generate(paths, output_root)
    print(f"Generated artifacts in: {output_root}")


if __name__ == "__main__":
    main()
