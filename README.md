# Advanced Software Engineering & DevOps

Course material of IT30213 Advanced Software Engineering & DevOps at Pradita University: the code of the fourteen sessions, each with a run guide, the book and the slides.

## What is where

| Folder or file | What it holds |
|---|---|
| `projects/session-XX/` | The code of session XX and its run guide, `README.md`. In sessions 1 to 3 the code and the guide sit in `python/`. |
| `module/` | The book, in Indonesian. `advanced-software-engineering.tex` puts the fourteen chapters together. |
| `slides/sessionXX/` | The slides of session XX, in Indonesian, with the shared Beamer theme in `slides/theme/`. |
| `figures/` | Figures that the book and the slides share. |
| `.github/workflows/` | The CI and CD workflows of session 4. |
| `deploy.sh` | Session 4's deployment by hand: runs the CI workflow and deploys only if the tests pass. |

## Sessions

| Session | Topic | Run guide |
|---|---|---|
| 1 | A video pipeline with ZeroMQ | [projects/session-01/python/README.md](projects/session-01/python/README.md) |
| 2 | Observability with Prometheus and Grafana | [projects/session-02/python/README.md](projects/session-02/python/README.md) |
| 3 | Security: encrypting the payload | [projects/session-03/python/README.md](projects/session-03/python/README.md) |
| 4 | A text-to-speech stack with CI/CD | [projects/session-04/README.md](projects/session-04/README.md) |
| 5 | Infrastructure as Code with Terraform | [projects/session-05/README.md](projects/session-05/README.md) |
| 6 | Orchestration with Kubernetes | [projects/session-06/README.md](projects/session-06/README.md) |
| 7 | Generating the infrastructure from a model | [projects/session-07/README.md](projects/session-07/README.md) |
| 8 | The MediaFlow metamodel | [projects/session-08/README.md](projects/session-08/README.md) |
| 9 | Textual DSLs with ANTLR, Xtext and Langium | [projects/session-09/README.md](projects/session-09/README.md) |
| 10 | A diagram editor with Sirius | [projects/session-10/README.md](projects/session-10/README.md) |
| 11 | Model-to-model transformation with ATL and ETL | [projects/session-11/README.md](projects/session-11/README.md) |
| 12 | Model-to-text transformation with EGX and EGL | [projects/session-12/README.md](projects/session-12/README.md) |
| 13 | Model validation with EVL and OCL | [projects/session-13/README.md](projects/session-13/README.md) |
| 14 | Multi-agent systems with MCP servers | [projects/session-14/README.md](projects/session-14/README.md) |

Sessions 1 to 7 build on each other: a video pipeline, then a text-to-speech system that later sessions deploy with Compose, Terraform, Kubernetes and a generator. Sessions 8 to 13 share the MediaFlow metamodel of session 8.

## Building the book and the slides

The book and the slides build with XeLaTeX:

```bash
cd module
latexmk -xelatex advanced-software-engineering.tex
cd ../slides/session07
latexmk -xelatex session07.tex
```

## License

MIT, see [LICENSE](LICENSE).
