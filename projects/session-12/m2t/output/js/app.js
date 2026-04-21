const SVG_NS = "http://www.w3.org/2000/svg";
const elk = new ELK();
const svg = document.getElementById("graph-svg");
const tooltip = document.getElementById("tooltip");
const fragment = document.getElementById("fragment");
const selector = document.getElementById("flow");

// Cache for fetched flow data
const dataCache = {};
const tableCache = {};

function el(name, attrs, text) {
  attrs = attrs || {};
  const n = document.createElementNS(SVG_NS, name);
  for (const k in attrs) n.setAttribute(k, attrs[k]);
  if (text !== undefined) n.textContent = text;
  return n;
}

async function loadData(name) {
  if (dataCache[name]) return dataCache[name];
  const resp = await fetch("elk/" + name + ".json");
  if (!resp.ok) return null;
  const data = await resp.json();
  dataCache[name] = data;
  return data;
}

async function loadTables(name) {
  if (tableCache[name]) return tableCache[name];
  const resp = await fetch("tables/" + name + ".tables.html");
  if (!resp.ok) return "";
  const html = await resp.text();
  tableCache[name] = html;
  return html;
}

function showTooltip(evt, html) {
  tooltip.innerHTML = html;
  tooltip.style.display = "block";
  const view = document.getElementById("graph-view");
  const vr = view.getBoundingClientRect();
  tooltip.style.left = (evt.clientX - vr.left + view.scrollLeft + 12) + "px";
  tooltip.style.top  = (evt.clientY - vr.top  + view.scrollTop  + 12) + "px";
}

function hideTooltip() { tooltip.style.display = "none"; }

async function render(name) {
  const data = await loadData(name);
  fragment.innerHTML = await loadTables(name);
  if (!data) return;
  const nodeMeta = data.nodeMeta || {};
  const edgeMeta = data.edgeMeta || {};
  svg.innerHTML = "";
  try {
    const layouted = await elk.layout(data.elk);
    const W = Math.ceil(layouted.width  || 800) + 40;
    const H = Math.ceil(layouted.height || 500) + 40;
    svg.setAttribute("viewBox", `0 0 ${W} ${H}`);
    svg.setAttribute("width",  W);
    svg.setAttribute("height", H);

    const defs = el("defs");
    defs.innerHTML =
      '<marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" ' +
      'markerWidth="7" markerHeight="7" orient="auto-start-reverse">' +
      '<path d="M0,0 L10,5 L0,10 Z" fill="#94a3b8"/></marker>';
    svg.appendChild(defs);

    const edgesG = el("g", {class: "edges"});
    for (const e of (layouted.edges || [])) {
      const g = el("g", {class: "edge"});
      const section = (e.sections && e.sections[0]) || null;
      if (section) {
        let d = `M ${section.startPoint.x} ${section.startPoint.y}`;
        for (const bp of (section.bendPoints || [])) d += ` L ${bp.x} ${bp.y}`;
        d += ` L ${section.endPoint.x} ${section.endPoint.y}`;
        g.appendChild(el("path", {d: d, "marker-end": "url(#arrow)"}));
        const em = edgeMeta[e.id];
        if (em) {
          g.addEventListener("mousemove", evt =>
            showTooltip(evt, `<div class="t">${em.name}</div>` +
                             `<div>${em.source}:${em.sourcePort} → ${em.target}:${em.targetPort}</div>`));
          g.addEventListener("mouseleave", hideTooltip);
        }
      }
      edgesG.appendChild(g);
    }
    svg.appendChild(edgesG);

    for (const n of (layouted.children || [])) {
      const kind = (nodeMeta[n.id] && nodeMeta[n.id].type) || "Node";
      const g = el("g", {class: "node " + kind,
                         transform: `translate(${n.x},${n.y})`});
      g.appendChild(el("rect", {width: n.width, height: n.height, rx: 8, ry: 8}));
      const label = (n.labels && n.labels[0] && n.labels[0].text) || n.id;
      g.appendChild(el("text",
        {x: n.width / 2, y: n.height / 2 - 2}, label));
      g.appendChild(el("text",
        {x: n.width / 2, y: n.height / 2 + 14, class: "kind"}, kind));
      for (const p of (n.ports || [])) {
        const pg = el("g", {class: "port",
                            transform: `translate(${p.x},${p.y})`});
        pg.appendChild(el("circle",
          {cx: (p.width||8)/2, cy: (p.height||8)/2, r: 4}));
        g.appendChild(pg);
      }
      const m = nodeMeta[n.id];
      let html = '';
      if (m) {
        html = `<div class="t">${m.name}</div><div>${m.type}</div>`;
        for (const k of Object.keys(m)) {
          if (k === "name" || k === "type") continue;
          const v = m[k];
          if (v === null || v === undefined || v === "") continue;
          html += `<pre>${k}: ${String(v)}</pre>`;
        }
      }
      g.addEventListener("mousemove", evt => showTooltip(evt, html));
      g.addEventListener("mouseleave", hideTooltip);
      svg.appendChild(g);
    }
  } catch (err) {
    svg.appendChild(el("text", {x: 20, y: 40, fill: "#ef4444"},
                        "ELK layout failed: " + err));
    console.error(err);
  }
}

// Discover available flows from manifest.json in the output root.
async function init() {
  try {
    const resp = await fetch("manifest.json");
    if (resp.ok) {
      const flows = await resp.json();
      selector.innerHTML = "";
      for (const name of flows) {
        const opt = document.createElement("option");
        opt.value = name;
        opt.textContent = name;
        selector.appendChild(opt);
      }
    }
  } catch (e) {
    // manifest.json not available; use options already in the HTML
  }

  if (selector.options.length > 0) {
    selector.value = selector.options[0].value;
    render(selector.value);
  }
}

selector.addEventListener("change", () => render(selector.value));
init();
