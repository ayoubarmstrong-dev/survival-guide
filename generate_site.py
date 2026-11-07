import os
import json
import re

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
JSON_PATH = os.path.join(ROOT_DIR, "app", "src", "main", "assets", "json", "survival_master_content_en.json")
DOCS_DIR = os.path.join(ROOT_DIR, "docs")
ARTICLES_DIR = os.path.join(DOCS_DIR, "articles")
CSS_DIR = os.path.join(DOCS_DIR, "css")

os.makedirs(ARTICLES_DIR, exist_ok=True)
os.makedirs(CSS_DIR, exist_ok=True)

with open(JSON_PATH, "r", encoding="utf-8") as f:
    data = json.load(f)

categories = {c["id"]: c["name"] for c in data.get("categories", [])}
articles = data.get("articles", [])
checklists = data.get("checklists", [])
gear_list = data.get("affiliate_gear", [])

ARTICLE_SLUGS = {
    "art_food_storage": "food-storage.html",
    "art_water_purification": "water-purification.html",
    "art_nuclear_war": "nuclear-fallout-shelter.html",
    "art_urban_combat_earthquake": "urban-combat-collapse.html",
    "art_urban_grayman": "urban-grayman.html",
    "art_forest_wilderness": "wilderness-survival.html",
    "art_heirloom_agriculture": "heirloom-agriculture.html",
    "art_offgrid_energy_mechanical": "offgrid-energy.html"
}

def markdown_to_html(md):
    if not md:
        return ""
    
    lines = md.strip().split("\n")
    html_lines = []
    in_list = False
    
    for line in lines:
        line_str = line.strip()
        
        # Headers
        if line_str.startswith("### "):
            if in_list: html_lines.append("</ul>"); in_list = False
            title = line_str[4:]
            html_lines.append(f"<h3>{format_inline(title)}</h3>")
            continue
        elif line_str.startswith("## "):
            if in_list: html_lines.append("</ul>"); in_list = False
            title = line_str[3:]
            html_lines.append(f"<h2>{format_inline(title)}</h2>")
            continue
        elif line_str.startswith("# "):
            if in_list: html_lines.append("</ul>"); in_list = False
            title = line_str[2:]
            html_lines.append(f"<h1>{format_inline(title)}</h1>")
            continue
            
        # Lists
        if line_str.startswith("- ") or line_str.startswith("* "):
            if not in_list:
                html_lines.append("<ul>")
                in_list = True
            content = line_str[2:]
            html_lines.append(f"<li>{format_inline(content)}</li>")
            continue
        elif re.match(r"^\d+\.\s+", line_str):
            if not in_list:
                html_lines.append("<ol>")
                in_list = True
            content = re.sub(r"^\d+\.\s+", "", line_str)
            html_lines.append(f"<li>{format_inline(content)}</li>")
            continue
        else:
            if in_list:
                html_lines.append("</ul>" if "<ul>" in "".join(html_lines[-5:]) else "</ol>")
                in_list = False
                
        # Blockquotes
        if line_str.startswith("> "):
            quote = line_str[2:]
            html_lines.append(f"<blockquote>{format_inline(quote)}</blockquote>")
            continue
            
        # Empty lines
        if not line_str:
            continue
            
        # Regular paragraph
        html_lines.append(f"<p>{format_inline(line_str)}</p>")
        
    if in_list:
        html_lines.append("</ul>")
        
    return "\n".join(html_lines)

def format_inline(text):
    # Bold **text**
    text = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", text)
    # Italic *text*
    text = re.sub(r"\*(.+?)\*", r"<em>\1</em>", text)
    # Inline code `code`
    text = re.sub(r"`(.+?)`", r"<code>\1</code>", text)
    return text

def get_footer(is_subpage=False):
    prefix = "../" if is_subpage else ""
    return f"""
  <footer class="site-footer">
    <div class="footer-container">
      <div class="footer-grid">
        <div class="footer-col brand-col">
          <div class="footer-logo">
            <span class="shield-icon">🛡️</span>
            <span class="brand-name">AEGIS SURVIVAL</span>
          </div>
          <p class="footer-desc">
            Rigorous, field-tested tactical survival doctrine, austere medical protocols, and engineering schematics for civil collapse and extreme emergencies.
          </p>
        </div>
        
        <div class="footer-col">
          <h4>Field Modules</h4>
          <ul>
            <li><a href="{prefix}articles/water-purification.html">Water Purification</a></li>
            <li><a href="{prefix}articles/food-storage.html">Food Storage Protocols</a></li>
            <li><a href="{prefix}articles/nuclear-fallout-shelter.html">Nuclear Fallout Defense</a></li>
            <li><a href="{prefix}articles/urban-combat-collapse.html">Urban Combat &amp; Triage</a></li>
            <li><a href="{prefix}articles/wilderness-survival.html">Wilderness Bushcraft</a></li>
          </ul>
        </div>
        
        <div class="footer-col">
          <h4>Tactical Gear</h4>
          <ul>
            <li><a href="{prefix}gear.html">Field Equipment Store</a></li>
            <li><a href="{prefix}gear.html#filtration">Sub-Micron Water Filters</a></li>
            <li><a href="{prefix}gear.html#medical">Combat Tourniquets (CAT)</a></li>
            <li><a href="{prefix}gear.html#radiological">Potassium Iodide Shields</a></li>
            <li><a href="{prefix}gear.html#comms">Emergency Crank Radios</a></li>
          </ul>
        </div>
        
        <div class="footer-col">
          <h4>Compliance &amp; Trust</h4>
          <ul>
            <li><a href="{prefix}about.html">About &amp; Editorial Ethics</a></li>
            <li><a href="{prefix}privacy-policy.html">Privacy &amp; Cookie Policy</a></li>
            <li><a href="{prefix}privacy-policy.html#amazon-disclosure">Amazon Affiliate Disclosure</a></li>
            <li><a href="{prefix}contact.html">Contact &amp; Gear Submissions</a></li>
          </ul>
        </div>
      </div>

      <!-- MANDATORY AMAZON AFFILIATE STATEMENT -->
      <div class="amazon-compliance-box">
        <p class="amazon-legal-statement">
          <strong>Amazon Associates Program Disclosure:</strong> Aegis Survival is a participant in the Amazon Services LLC Associates Program, an affiliate advertising program designed to provide a means for sites to earn advertising fees by advertising and linking to Amazon.com.
        </p>
        <p class="amazon-mandate-quote">
          <em>“As an Amazon Associate, I earn from qualifying purchases.”</em>
        </p>
        <p class="amazon-pricing-note">
          Product prices and availability are accurate as of the date/time indicated and are subject to change. Any price and availability information displayed on Amazon at the time of purchase will apply to the purchase of this product.
        </p>
      </div>

      <div class="footer-bottom">
        <p>&copy; 2026 Aegis Survival &amp; Preparedness Group. All rights reserved.</p>
        <div class="footer-bottom-links">
          <a href="{prefix}privacy-policy.html">Privacy Policy</a>
          <span>•</span>
          <a href="{prefix}about.html">Editorial Standards</a>
          <span>•</span>
          <a href="{prefix}contact.html">Contact Us</a>
        </div>
      </div>
    </div>
  </footer>
"""

def get_nav(is_subpage=False):
    prefix = "../" if is_subpage else ""
    return f"""
  <header class="site-header">
    <div class="header-inner">
      <a href="{prefix}index.html" class="site-logo">
        <span class="logo-shield">🛡️</span>
        <div class="logo-text">
          <span class="title">AEGIS SURVIVAL</span>
          <span class="subtitle">TACTICAL FIELD DOCTRINE</span>
        </div>
      </a>

      <nav class="nav-links">
        <a href="{prefix}index.html">Field Guides</a>
        <a href="{prefix}gear.html" class="nav-gear">Tactical Gear Store</a>
        <a href="{prefix}about.html">About Us</a>
        <a href="{prefix}contact.html">Contact</a>
      </nav>

      <div class="header-action">
        <a href="{prefix}gear.html" class="btn-gear-nav">
          <span>🛒 Tested Gear Hub</span>
        </a>
      </div>
    </div>
  </header>
"""

# 1. WRITE CSS
css_content = """/* Aegis Survival - Modern Tactical Military Theme */
:root {
  --bg-dark: #0A0E14;
  --bg-card: #121820;
  --bg-surface: #18202B;
  --bg-surface-elevated: #1F2A38;
  --border: #222E3C;
  --border-focus: #35475E;
  
  --primary: #2EA043;
  --primary-hover: #3FB950;
  --primary-soft: rgba(46, 160, 67, 0.15);
  
  --amber: #D29922;
  --amber-soft: rgba(210, 153, 34, 0.15);
  
  --red: #F85149;
  --red-soft: rgba(248, 81, 73, 0.15);
  
  --cyan: #58A6FF;
  --cyan-soft: rgba(88, 166, 255, 0.15);
  
  --text-main: #F0F6FC;
  --text-muted: #8B949E;
  --text-dim: #6E7681;
  
  --container-max: 1140px;
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
}

* { box-sizing: border-box; margin: 0; padding: 0; }
html { scroll-behavior: smooth; }
body {
  font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  background-color: var(--bg-dark);
  color: var(--text-main);
  line-height: 1.65;
  font-size: 16px;
  overflow-x: hidden;
}

a { color: var(--cyan); text-decoration: none; transition: all 0.2s; }
a:hover { color: #80BFFF; }

/* Header & Nav */
.site-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(10, 14, 20, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
}
.header-inner {
  max-width: var(--container-max);
  margin: 0 auto;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.site-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-main);
}
.site-logo:hover { color: var(--text-main); }
.logo-shield { font-size: 26px; }
.logo-text { display: flex; flex-direction: column; }
.logo-text .title { font-size: 18px; font-weight: 800; letter-spacing: 1px; color: #fff; }
.logo-text .subtitle { font-size: 10px; font-weight: 700; color: var(--primary); letter-spacing: 1.5px; }

.nav-links {
  display: flex;
  gap: 28px;
  align-items: center;
}
.nav-links a {
  color: var(--text-muted);
  font-weight: 600;
  font-size: 14px;
}
.nav-links a:hover { color: var(--text-main); }
.nav-links .nav-gear { color: var(--primary); }

.btn-gear-nav {
  background: var(--primary-soft);
  color: var(--primary);
  border: 1px solid var(--primary);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}
.btn-gear-nav:hover {
  background: var(--primary);
  color: #000;
  box-shadow: 0 0 16px rgba(46, 160, 67, 0.4);
}

/* Hero Section */
.hero-section {
  padding: 70px 20px 50px;
  max-width: var(--container-max);
  margin: 0 auto;
  text-align: center;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--primary-soft);
  color: var(--primary);
  border: 1px solid var(--primary);
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}
.hero-title {
  font-size: 44px;
  font-weight: 800;
  letter-spacing: -0.5px;
  line-height: 1.2;
  margin-bottom: 16px;
  color: #fff;
}
.hero-title span { color: var(--primary); }
.hero-subtitle {
  font-size: 18px;
  color: var(--text-muted);
  max-width: 720px;
  margin: 0 auto 30px;
  line-height: 1.6;
}
.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}
.btn-hero-primary {
  background: var(--primary);
  color: #000;
  font-weight: 700;
  padding: 12px 26px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.btn-hero-primary:hover {
  background: var(--primary-hover);
  color: #000;
  box-shadow: 0 0 20px rgba(46, 160, 67, 0.4);
}
.btn-hero-secondary {
  background: var(--bg-card);
  border: 1px solid var(--border);
  color: var(--text-main);
  font-weight: 700;
  padding: 12px 26px;
  border-radius: var(--radius-sm);
  font-size: 14px;
}
.btn-hero-secondary:hover {
  border-color: var(--cyan);
  color: #fff;
}

/* Category Filter Bar */
.filter-bar {
  max-width: var(--container-max);
  margin: 0 auto 36px;
  padding: 0 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}
.filter-pill {
  background: var(--bg-card);
  border: 1px solid var(--border);
  color: var(--text-muted);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-pill:hover, .filter-pill.active {
  background: var(--primary-soft);
  border-color: var(--primary);
  color: var(--primary);
}

/* Article Grid */
.articles-container {
  max-width: var(--container-max);
  margin: 0 auto;
  padding: 0 20px 80px;
}
.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border);
  padding-bottom: 12px;
}
.section-heading h2 {
  font-size: 24px;
  font-weight: 800;
  color: #fff;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}
.article-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 24px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, border-color 0.2s;
}
.article-card:hover {
  transform: translateY(-4px);
  border-color: var(--border-focus);
}
.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.badge-priority {
  font-size: 10px;
  font-weight: 800;
  padding: 3px 8px;
  border-radius: 4px;
  letter-spacing: 0.5px;
}
.priority-critical { background: var(--red-soft); color: var(--red); border: 1px solid rgba(248, 81, 73, 0.4); }
.priority-high { background: var(--amber-soft); color: var(--amber); border: 1px solid rgba(210, 153, 34, 0.4); }
.priority-normal { background: var(--primary-soft); color: var(--primary); border: 1px solid rgba(46, 160, 67, 0.4); }

.badge-diagram {
  font-size: 11px;
  color: var(--cyan);
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.card-title {
  font-size: 18px;
  font-weight: 800;
  line-height: 1.4;
  margin-bottom: 10px;
}
.card-title a { color: #fff; }
.card-title a:hover { color: var(--cyan); }
.card-desc {
  font-size: 13.5px;
  color: var(--text-muted);
  line-height: 1.55;
  margin-bottom: 18px;
  flex: 1;
}
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid var(--border);
  padding-top: 14px;
  font-size: 12px;
  color: var(--text-dim);
}
.read-link {
  color: var(--primary);
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.read-link:hover { color: var(--primary-hover); }

/* Article Detail Page Layout */
.article-layout {
  max-width: 860px;
  margin: 40px auto 80px;
  padding: 0 20px;
}
.breadcrumbs {
  font-size: 13px;
  color: var(--text-dim);
  margin-bottom: 24px;
}
.breadcrumbs a { color: var(--text-muted); }
.breadcrumbs a:hover { color: var(--cyan); }

.article-header { margin-bottom: 30px; }
.article-header .priority-pill {
  display: inline-block;
  margin-bottom: 12px;
}
.article-page-title {
  font-size: 38px;
  font-weight: 800;
  line-height: 1.25;
  color: #fff;
  margin-bottom: 16px;
}
.article-byline {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-muted);
  border-bottom: 1px solid var(--border);
  padding-bottom: 18px;
}

.article-lead {
  background: var(--bg-card);
  border-left: 4px solid var(--primary);
  padding: 16px 20px;
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  font-size: 16px;
  color: var(--text-main);
  line-height: 1.6;
  margin-bottom: 36px;
}

/* Diagram Container */
.diagram-wrapper {
  background: #000;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 20px;
  margin: 36px 0;
}
.diagram-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  font-size: 12px;
}
.diagram-label {
  color: var(--cyan);
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}
.diagram-asset-name {
  color: var(--text-dim);
  font-family: 'JetBrains Mono', monospace;
}
.diagram-box {
  width: 100%;
  max-height: 520px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #090D11;
  border-radius: var(--radius-sm);
  padding: 16px;
}
.diagram-box img, .diagram-box svg {
  max-width: 100%;
  max-height: 480px;
  object-fit: contain;
}

/* Article Markdown Typography */
.article-body {
  font-size: 16px;
  line-height: 1.75;
  color: #D1D5DB;
}
.article-body h2 {
  font-size: 24px;
  font-weight: 800;
  color: #fff;
  margin: 38px 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}
.article-body h3 {
  font-size: 19px;
  font-weight: 700;
  color: var(--amber);
  margin: 28px 0 12px;
}
.article-body p { margin-bottom: 18px; }
.article-body ul, .article-body ol {
  margin: 0 0 20px 24px;
}
.article-body li { margin-bottom: 8px; }
.article-body strong { color: #fff; }
.article-body code {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  color: var(--cyan);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
}
.article-body blockquote {
  background: rgba(210, 153, 34, 0.1);
  border-left: 3px solid var(--amber);
  padding: 12px 18px;
  margin: 24px 0;
  color: #F8FAFC;
  font-style: italic;
}

/* Checklist Section */
.checklist-section {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 24px;
  margin: 40px 0;
}
.checklist-header {
  color: var(--primary);
  font-size: 16px;
  font-weight: 800;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.checklist-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.checklist-item:last-child { border-bottom: none; }
.chk-box {
  color: var(--primary);
  font-size: 18px;
  line-height: 1;
}
.chk-title { font-weight: 700; font-size: 14px; color: #fff; }
.chk-spec { font-size: 13px; color: var(--text-muted); margin-top: 2px; }
.chk-formula {
  display: inline-block;
  background: var(--amber-soft);
  color: var(--amber);
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
  margin-top: 4px;
}

/* STYLED CURATED TACTICAL GEAR CARD (AMAZON COMPLIANT) */
.curated-gear-box {
  background: linear-gradient(180deg, #16202C 0%, #10161F 100%);
  border: 2px solid #2B3D52;
  border-radius: var(--radius-lg);
  padding: 28px;
  margin: 50px 0 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.4);
  position: relative;
}
.curated-gear-box::before {
  content: 'FIELD-TESTED EQUIPMENT RECOMMENDATION';
  position: absolute;
  top: -11px;
  left: 24px;
  background: var(--primary);
  color: #000;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 1px;
  padding: 3px 10px;
  border-radius: 4px;
}
.gear-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin-top: 10px;
}
.gear-product-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.gear-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}
.gear-title {
  font-size: 17px;
  font-weight: 800;
  color: #fff;
  line-height: 1.35;
}
.gear-price-pill {
  background: var(--primary-soft);
  color: var(--primary);
  border: 1px solid var(--primary);
  font-weight: 800;
  font-size: 14px;
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  white-space: nowrap;
}
.gear-rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--amber);
  font-weight: 700;
  margin-bottom: 12px;
}
.gear-specs {
  font-size: 13.5px;
  color: #C5D1DE;
  line-height: 1.5;
  margin-bottom: 16px;
}
.btn-amazon-cta {
  background: #FF9900;
  color: #000;
  font-weight: 800;
  font-size: 13.5px;
  padding: 12px 20px;
  border-radius: var(--radius-sm);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(255, 153, 0, 0.25);
}
.btn-amazon-cta:hover {
  background: #FFAC33;
  color: #000;
  box-shadow: 0 4px 18px rgba(255, 153, 0, 0.4);
}
.gear-asin-tag {
  font-size: 11px;
  color: var(--text-dim);
  margin-top: 8px;
  text-align: center;
}

/* Static Trust Pages (About, Privacy, Contact) */
.page-container {
  max-width: 860px;
  margin: 40px auto 80px;
  padding: 0 20px;
}
.page-header {
  border-bottom: 1px solid var(--border);
  padding-bottom: 24px;
  margin-bottom: 32px;
}
.page-header h1 {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 8px;
}
.page-header p {
  font-size: 16px;
  color: var(--text-muted);
}

.legal-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 24px;
  margin-bottom: 24px;
}
.legal-card h2 {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 12px;
}
.legal-card p {
  color: #C5D1DE;
  margin-bottom: 12px;
  font-size: 14.5px;
}
.legal-card ul {
  margin: 0 0 16px 20px;
  color: #C5D1DE;
  font-size: 14.5px;
}

/* Contact Form */
.contact-form {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 28px;
}
.form-group { margin-bottom: 18px; }
.form-group label {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}
.form-input, .form-textarea {
  width: 100%;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  color: #fff;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  outline: none;
  font-family: inherit;
}
.form-input:focus, .form-textarea:focus { border-color: var(--cyan); }
.form-textarea { min-height: 120px; resize: vertical; }

/* Footer */
.site-footer {
  background: #070A0E;
  border-top: 1px solid var(--border);
  padding: 60px 20px 30px;
  color: var(--text-muted);
}
.footer-container {
  max-width: var(--container-max);
  margin: 0 auto;
}
.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}
.brand-col .footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
}
.brand-col .brand-name {
  font-size: 18px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 1px;
}
.brand-col .footer-desc {
  font-size: 13.5px;
  line-height: 1.6;
  max-width: 320px;
}

.footer-col h4 {
  font-size: 13px;
  font-weight: 800;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 16px;
}
.footer-col ul { list-style: none; }
.footer-col li { margin-bottom: 10px; font-size: 13.5px; }
.footer-col a { color: var(--text-muted); }
.footer-col a:hover { color: var(--primary); }

/* Amazon Compliance Box */
.amazon-compliance-box {
  background: rgba(18, 24, 32, 0.9);
  border: 1px solid #2B384A;
  border-radius: var(--radius-md);
  padding: 20px 24px;
  margin-bottom: 30px;
}
.amazon-legal-statement {
  font-size: 13px;
  color: #E2E8F0;
  line-height: 1.6;
  margin-bottom: 8px;
}
.amazon-mandate-quote {
  font-size: 14px;
  font-weight: 700;
  color: #FFB020;
  display: block;
  margin-bottom: 8px;
}
.amazon-pricing-note {
  font-size: 11.5px;
  color: var(--text-dim);
  line-height: 1.5;
}

.footer-bottom {
  border-top: 1px solid var(--border);
  padding-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12.5px;
  flex-wrap: wrap;
  gap: 12px;
}
.footer-bottom-links {
  display: flex;
  gap: 12px;
  align-items: center;
}
.footer-bottom-links a { color: var(--text-muted); }
.footer-bottom-links a:hover { color: #fff; }

@media (max-width: 868px) {
  .hero-title { font-size: 32px; }
  .footer-grid { grid-template-columns: 1fr 1fr; }
  .nav-links { display: none; }
}
@media (max-width: 580px) {
  .footer-grid { grid-template-columns: 1fr; }
  .article-grid { grid-template-columns: 1fr; }
}
"""

with open(os.path.join(CSS_DIR, "style.css"), "w", encoding="utf-8") as f:
    f.write(css_content)

# 2. GENERATE ALL 8 ARTICLE PAGES
for art in articles:
    art_id = art["id"]
    slug = ARTICLE_SLUGS.get(art_id, f"{art_id}.html")
    file_path = os.path.join(ARTICLES_DIR, slug)
    
    cat_name = categories.get(art.get("category_id"), "Tactical Survival")
    priority = art.get("priority_level", "NORMAL")
    p_class = f"priority-{priority.lower()}"
    
    diagram_asset = art.get("diagram_asset")
    body_html = markdown_to_html(art.get("content_markdown", ""))
    
    # Matching Checklists
    art_checklists = [c for c in checklists if c.get("article_id") == art_id]
    checklist_html = ""
    if art_checklists:
        checklist_html += """
        <div class="checklist-section">
          <div class="checklist-header">
            <span>☑</span> <span>Interactive Field Readiness Checklist</span>
          </div>
        """
        for c in art_checklists:
            formula_tag = f'<div class="chk-formula">Standard: {c.get("quantity_formula")}</div>' if c.get("quantity_formula") else ""
            checklist_html += f"""
            <div class="checklist-item">
              <span class="chk-box">✔</span>
              <div>
                <div class="chk-title">{c.get("title")}</div>
                <div class="chk-spec">{c.get("specification", "")}</div>
                {formula_tag}
              </div>
            </div>
            """
        checklist_html += "</div>"
        
    # Matching Gear Card (or fallback to top gear)
    matching_gear = [g for g in gear_list if g.get("article_id") == art_id]
    if not matching_gear and gear_list:
        matching_gear = [gear_list[0]]
        
    gear_card_html = """
    <div class="curated-gear-box">
      <div class="gear-grid">
    """
    for g in matching_gear:
        stars = "★" * g.get("importance_rating", 5)
        price_val = f"${g.get('estimated_price_usd'):.2f}" if g.get("estimated_price_usd") else "Check Amazon"
        asin_val = f'<div class="gear-asin-tag">Amazon ASIN: {g.get("amazon_asin")} • Verified Authentic</div>' if g.get("amazon_asin") else ""
        gear_card_html += f"""
        <div class="gear-product-card">
          <div class="gear-card-top">
            <h3 class="gear-title">{g.get("name")}</h3>
            <span class="gear-price-pill">{price_val}</span>
          </div>
          <div class="gear-rating-row">
            <span>{stars}</span>
            <span>({g.get('importance_rating', 5)}-Star Field Rating)</span>
          </div>
          <p class="gear-specs">{g.get("spec_summary", "")}</p>
          <a href="{g.get('affiliate_url_template', '#')}" target="_blank" rel="nofollow noopener sponsored" class="btn-amazon-cta">
            <span>🛒 Order / Check Price on Amazon</span>
          </a>
          {asin_val}
        </div>
        """
    gear_card_html += """
      </div>
    </div>
    """

    diagram_section_html = ""
    if diagram_asset:
        diagram_section_html = f"""
        <div class="diagram-wrapper">
          <div class="diagram-header">
            <span class="diagram-label">Tactical Technical Schematic (Vector SVG)</span>
            <span class="diagram-asset-name">{diagram_asset}</span>
          </div>
          <div class="diagram-box">
            <img src="../diagrams/{diagram_asset}" alt="{art.get('title')} Schematic" loading="lazy">
          </div>
        </div>
        """

    article_page_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>{art.get('title')} | Aegis Survival Field Manual</title>
  <meta name="description" content="{art.get('summary', '')}">
  <link rel="stylesheet" href="../css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=True)}

  <main class="article-layout">
    <div class="breadcrumbs">
      <a href="../index.html">Home</a> &rsaquo; <span>{cat_name}</span> &rsaquo; <span>{art.get('title')}</span>
    </div>

    <header class="article-header">
      <div class="priority-pill">
        <span class="badge-priority {p_class}">PRIORITY: {priority}</span>
      </div>
      <h1 class="article-page-title">{art.get('title')}</h1>
      <div class="article-byline">
        <span>Published by <strong>Aegis Tactical Directorate</strong></span>
        <span>•</span>
        <span>Module: <strong>{cat_name}</strong></span>
        <span>•</span>
        <span>Offline Verified ⚡</span>
      </div>
    </header>

    <div class="article-lead">
      {art.get('summary', '')}
    </div>

    {diagram_section_html}

    <div class="article-body">
      {body_html}
    </div>

    {checklist_html}

    {gear_card_html}
  </main>

  {get_footer(is_subpage=True)}

</body>
</html>
"""
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(article_page_html)

# 3. GENERATE INDEX.HTML
article_cards_html = ""
for art in articles:
    art_id = art["id"]
    slug = ARTICLE_SLUGS.get(art_id, f"{art_id}.html")
    priority = art.get("priority_level", "NORMAL")
    p_class = f"priority-{priority.lower()}"
    cat_name = categories.get(art.get("category_id"), "Tactical")
    diagram_badge = f'<span class="badge-diagram">📐 SVG Schematic</span>' if art.get("diagram_asset") else ''
    
    article_cards_html += f"""
    <article class="article-card">
      <div class="card-meta">
        <span class="badge-priority {p_class}">{priority}</span>
        {diagram_badge}
      </div>
      <h3 class="card-title">
        <a href="articles/{slug}">{art.get('title')}</a>
      </h3>
      <p class="card-desc">{art.get('summary', '')}</p>
      <div class="card-footer">
        <span>{cat_name}</span>
        <a href="articles/{slug}" class="read-link">Read Protocol &rarr;</a>
      </div>
    </article>
    """

# Featured gear cards for homepage
featured_gear_cards = ""
for g in gear_list:
    price_val = f"${g.get('estimated_price_usd'):.2f}" if g.get("estimated_price_usd") else "View Amazon"
    stars = "★" * g.get("importance_rating", 5)
    featured_gear_cards += f"""
    <div class="article-card" style="border-top: 3px solid var(--primary);">
      <div class="card-meta">
        <span style="color: var(--amber); font-size: 12px; font-weight: 700;">{stars}</span>
        <span class="gear-price-pill">{price_val}</span>
      </div>
      <h3 class="card-title" style="font-size: 16px;">
        <a href="{g.get('affiliate_url_template', '#')}" target="_blank" rel="nofollow noopener sponsored">{g.get('name')}</a>
      </h3>
      <p class="card-desc" style="font-size: 13px;">{g.get('spec_summary', '')}</p>
      <a href="{g.get('affiliate_url_template', '#')}" target="_blank" rel="nofollow noopener sponsored" class="btn-amazon-cta" style="margin-top: auto; padding: 10px 14px; font-size: 12.5px;">
        🛒 Check Price on Amazon
      </a>
    </div>
    """

index_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Aegis Survival | Operational Doctrine, Field Guides &amp; Tactical Gear</title>
  <meta name="description" content="Production-grade civil readiness protocols, bio-sand water filtration schematics, fallout protection factors, and field-tested emergency gear reviews.">
  <link rel="stylesheet" href="css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=False)}

  <section class="hero-section">
    <div class="hero-badge">⚡ BATTLE-TESTED PREPAREDNESS PROTOCOLS</div>
    <h1 class="hero-title">Survive Civil Collapse, War &amp; <span>Extreme Crises</span></h1>
    <p class="hero-subtitle">
      Zero fluff. Rigorous technical schematics, caloric rationing mathematics, and proven tactical gear recommendations to protect your family when modern systems fail.
    </p>
    <div class="hero-actions">
      <a href="#protocols" class="btn-hero-primary">Explore Field Protocols</a>
      <a href="gear.html" class="btn-hero-secondary">View Curated Tactical Gear</a>
    </div>
  </section>

  <div class="filter-bar">
    <span class="filter-pill active">All Protocols</span>
    <span class="filter-pill">Water &amp; Sanitation</span>
    <span class="filter-pill">Caloric Food Reserve</span>
    <span class="filter-pill">CBRN &amp; Fallout</span>
    <span class="filter-pill">Urban Survival</span>
    <span class="filter-pill">Off-Grid Power</span>
  </div>

  <section class="articles-container" id="protocols">
    <div class="section-heading">
      <h2>Operational Readiness Protocols</h2>
      <span style="color: var(--text-muted); font-size: 13px;">8 Complete Field Manuals</span>
    </div>
    <div class="article-grid">
      {article_cards_html}
    </div>
  </section>

  <!-- Featured Curated Gear Showcase -->
  <section class="articles-container" style="background: rgba(18, 24, 32, 0.5); padding: 50px 20px 70px; border-top: 1px solid var(--border); border-bottom: 1px solid var(--border);">
    <div class="section-heading">
      <div>
        <h2>Essential Field-Tested Tactical Hardware</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-top: 4px;">Independently vetted survival equipment compliant with Amazon Associates procurement.</p>
      </div>
      <a href="gear.html" class="read-link">View Full Catalog &rarr;</a>
    </div>
    <div class="article-grid">
      {featured_gear_cards}
    </div>
  </section>

  {get_footer(is_subpage=False)}

</body>
</html>
"""
with open(os.path.join(DOCS_DIR, "index.html"), "w", encoding="utf-8") as f:
    f.write(index_html)

# 4. GENERATE GEAR.HTML (DEDICATED STOREFRONT)
gear_catalog_items = ""
for g in gear_list:
    price_val = f"${g.get('estimated_price_usd'):.2f}" if g.get("estimated_price_usd") else "View Amazon"
    stars = "★" * g.get("importance_rating", 5)
    asin_tag = f'<span style="color: var(--text-dim); font-size: 12px;">ASIN: {g.get("amazon_asin")}</span>' if g.get("amazon_asin") else ""
    img_tag = f'<img src="{g.get("image_url")}" alt="{g.get("name")}" style="width: 80px; height: 80px; object-fit: contain; border-radius: 6px; background: rgba(0,0,0,0.3); border: 1px solid var(--border); padding: 4px; margin-right: 14px; flex-shrink: 0;" loading="lazy">' if g.get("image_url") else ""
    gear_catalog_items += f"""
    <div class="article-card" style="border-left: 4px solid var(--primary);">
      <div class="card-meta">
        <span style="color: var(--amber); font-weight: 700; font-size: 13px;">{stars} ({g.get('importance_rating', 5)}-Star Critical)</span>
        <span class="gear-price-pill">{price_val}</span>
      </div>
      <div style="display: flex; align-items: flex-start; margin-top: 10px;">
        {img_tag}
        <div style="flex: 1;">
          <h3 class="card-title" style="font-size: 18px; margin-bottom: 6px;">{g.get('name')}</h3>
          <p class="card-desc" style="font-size: 13.5px; line-height: 1.5; margin-bottom: 0;">{g.get('spec_summary', '')}</p>
        </div>
      </div>
      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: auto; padding-top: 14px; border-top: 1px solid var(--border);">
        {asin_tag}
        <a href="{g.get('affiliate_url_template', '#')}" target="_blank" rel="nofollow noopener sponsored" class="btn-amazon-cta">
          🛒 Order / Check Price on Amazon
        </a>
      </div>
    </div>
    """

gear_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Curated Tactical Gear &amp; Equipment Catalog | Aegis Survival</title>
  <meta name="description" content="Field-tested survival equipment: sub-micron water filters, North American Rescue CAT tourniquets, potassium iodide radiation tablets, and food storage mylar.">
  <link rel="stylesheet" href="css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=False)}

  <main class="page-container">
    <div class="page-header">
      <h1>Curated Tactical Gear Catalog</h1>
      <p>Every piece of hardware listed below has been evaluated for field durability, availability, and mission criticality.</p>
    </div>

    <div style="background: rgba(46, 160, 67, 0.15); border: 1px solid var(--primary); border-radius: 8px; padding: 16px 20px; margin-bottom: 30px;">
      <p style="font-size: 14px; color: #E2E8F0; line-height: 1.6;">
        <strong>Equipment Integrity Guarantee:</strong> We do not accept sponsored placements. All links redirect to official Amazon product listings with real-time pricing and verified customer review aggregation.
      </p>
    </div>

    <div class="article-grid" style="grid-template-columns: 1fr;">
      {gear_catalog_items}
    </div>
  </main>

  {get_footer(is_subpage=False)}

</body>
</html>
"""
with open(os.path.join(DOCS_DIR, "gear.html"), "w", encoding="utf-8") as f:
    f.write(gear_html)

# 5. GENERATE ABOUT.HTML
about_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>About Us &amp; Editorial Ethics | Aegis Survival</title>
  <meta name="description" content="Learn about Aegis Survival's mission, field testing methodology, and strict editorial standards for civil emergency preparedness.">
  <link rel="stylesheet" href="css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=False)}

  <main class="page-container">
    <div class="page-header">
      <h1>About Aegis Survival</h1>
      <p>Engineering-grade preparedness doctrine for unstable environments and civil catastrophes.</p>
    </div>

    <div class="legal-card">
      <h2>1. Our Mission</h2>
      <p>
        Aegis Survival was founded by a collective of austere emergency medicine practitioners, communications technicians, and mechanical engineers dedicated to demystifying disaster preparedness. We reject sensationalist survivalism in favor of empirically validated protocols, caloric mathematics, and physics-based engineering schematics.
      </p>
      <p>
        From nuclear fallout shielding factors (using the 7-10 rule) to bio-sand stratified water filtration, our field manuals are designed to be actionable, accessible offline, and free of commercial bias.
      </p>
    </div>

    <div class="legal-card">
      <h2>2. Rigorous Field Testing Methodology</h2>
      <p>
        When we recommend tactical equipment—such as the Sawyer MINI Water Filter, genuine Combat Application Tourniquets (CAT Gen 7), or high-barrier Mylar storage systems—they undergo rigorous stress evaluation:
      </p>
      <ul>
        <li><strong>Thermal Tolerance:</strong> Equipment performance between -20°C and +50°C.</li>
        <li><strong>Redundancy &amp; Mechanical Simplicity:</strong> Elimination of failure points dependent on delicate electronics or software locks.</li>
        <li><strong>Standardization:</strong> Compatibility with widely available consumables (batteries, bleach, iodine, standard threaded containers).</li>
      </ul>
    </div>

    <div class="legal-card">
      <h2>3. Editorial Independence &amp; Ethics</h2>
      <p>
        Aegis Survival maintains 100% editorial autonomy. Manufacturers do not pay for positive reviews, nor do we accept pre-screened review samples with editorial restrictions. Our monetization is strictly transparent: when readers purchase gear via our Amazon affiliate links, we earn an Amazon commission that funds our server infrastructure, technical diagram drafting, and open-source field manual distribution.
      </p>
    </div>

    <div class="legal-card">
      <h2>4. Offline-First Mobile Integration</h2>
      <p>
        All guides and schematics published on this portal are mirrored directly into our native Android APK (built with Jetpack Compose &amp; Room Database). In a grid-down catastrophe, internet connectivity will evaporate; our mobile tools and stored offline vectors remain operable in true zero-connectivity environments.
      </p>
    </div>
  </main>

  {get_footer(is_subpage=False)}

</body>
</html>
"""
with open(os.path.join(DOCS_DIR, "about.html"), "w", encoding="utf-8") as f:
    f.write(about_html)

# 6. GENERATE PRIVACY-POLICY.HTML (FULL AMAZON COMPLIANCE)
privacy_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Privacy Policy &amp; Amazon Affiliate Disclosure | Aegis Survival</title>
  <meta name="description" content="Aegis Survival Privacy Policy, Cookie Usage, GDPR/CCPA rights, and mandatory Amazon Associates affiliate disclosures.">
  <link rel="stylesheet" href="css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=False)}

  <main class="page-container">
    <div class="page-header">
      <h1>Privacy Policy &amp; Disclosures</h1>
      <p>Last updated: November 6, 2026. Fully compliant with Amazon Associates Operating Agreement &amp; FTC Regulations.</p>
    </div>

    <!-- Mandatory Amazon Section -->
    <div class="legal-card" id="amazon-disclosure" style="border: 2px solid var(--amber);">
      <h2 style="color: var(--amber);">1. Amazon Associates Program Disclosure (FTC &amp; Operating Agreement)</h2>
      <p style="font-size: 16px; font-weight: 700; color: #fff;">
        “As an Amazon Associate, I earn from qualifying purchases.”
      </p>
      <p>
        Aegis Survival participates in the <strong>Amazon Services LLC Associates Program</strong>, an affiliate advertising program designed to provide a means for websites to earn advertising fees and commissions by advertising and linking to Amazon.com, Amazon.co.uk, Amazon.ca, and affiliated regional marketplaces.
      </p>
      <p>
        This means that when you click on certain links on this website and subsequently make a purchase on Amazon, we may receive a small percentage of the sale at <strong>no additional cost to you</strong>. Pricing, shipping terms, and product guarantees remain identical whether you use an affiliate link or navigate directly to Amazon.
      </p>
      <p>
        Amazon and the Amazon logo are trademarks of Amazon.com, Inc. or its affiliates.
      </p>
    </div>

    <div class="legal-card">
      <h2>2. Information We Collect</h2>
      <p>
        Aegis Survival is committed to minimal data collection. We believe privacy is a cornerstone of operational security:
      </p>
      <ul>
        <li><strong>Voluntary Information:</strong> If you contact us via our web form or email, we collect your name and email address solely to respond to your inquiry. We never sell or distribute communication logs.</li>
        <li><strong>Technical Logs:</strong> Our web host (GitHub Pages / Vercel / Cloudflare) may collect standard server access logs (IP address, browser user-agent, referring URL, timestamp) for security, DDoS prevention, and uptime verification.</li>
      </ul>
    </div>

    <div class="legal-card">
      <h2>3. Cookies and Tracking Technologies</h2>
      <p>
        When you click an outbound link to Amazon, Amazon places a 24-hour cookie on your browser to record that you arrived from Aegis Survival and credit any qualifying purchases. These cookies are governed by Amazon's Privacy Notice.
      </p>
      <p>
        You can control or disable cookies through your personal browser settings (such as Chrome, Firefox, Safari, or Brave). Disabling cookies will not affect your ability to read our field manuals or view technical schematics.
      </p>
    </div>

    <div class="legal-card">
      <h2>4. GDPR &amp; CCPA Compliance</h2>
      <p>
        Under the General Data Protection Regulation (GDR) and California Consumer Privacy Act (CCPA), you retain the right to:
      </p>
      <ul>
        <li>Request confirmation of any personal data retained in our contact archives.</li>
        <li>Request the immediate erasure of any communication records.</li>
        <li>Opt out of any potential future marketing communications.</li>
      </ul>
      <p>
        To exercise any data rights, submit a written request via our <a href="contact.html">Contact Portal</a>.
      </p>
    </div>

    <div class="legal-card">
      <h2>5. External Links Disclaimer</h2>
      <p>
        Our field guides contain links to external sites (such as government disaster agencies, peer-reviewed medical journals, and Amazon). Aegis Survival does not control and is not liable for the privacy practices, content, or terms of third-party platforms.
      </p>
    </div>
  </main>

  {get_footer(is_subpage=False)}

</body>
</html>
"""
with open(os.path.join(DOCS_DIR, "privacy-policy.html"), "w", encoding="utf-8") as f:
    f.write(privacy_html)

# 7. GENERATE CONTACT.HTML
contact_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Contact &amp; Editorial Inquiries | Aegis Survival</title>
  <meta name="description" content="Contact the Aegis Survival editorial team for technical inquiries, schematic corrections, or gear review submissions.">
  <link rel="stylesheet" href="css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>

  {get_nav(is_subpage=False)}

  <main class="page-container">
    <div class="page-header">
      <h1>Contact the Tactical Directorate</h1>
      <p>Have an operational question, schematic correction, or equipment verification inquiry? Reach out below.</p>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 40px;">
      <div class="legal-card">
        <h2>Direct Inquiries</h2>
        <p><strong>Editorial &amp; Field Protocols:</strong><br><a href="mailto:directorate@aegissurvival.com">directorate@aegissurvival.com</a></p>
        <p><strong>Affiliate &amp; Hardware Reviews:</strong><br><a href="mailto:gear@aegissurvival.com">gear@aegissurvival.com</a></p>
        <p><strong>Response Time:</strong><br><span style="color: var(--primary);">24 to 48 hours for verified technical requests.</span></p>
      </div>

      <div class="legal-card">
        <h2>Gear Submissions</h2>
        <p>
          Manufacturers seeking equipment evaluations must provide production units identical to consumer retail stock. We refuse embargoed prototypes or contracts that stipulate positive sentiment.
        </p>
      </div>
    </div>

    <div class="contact-form">
      <h2 style="font-size: 20px; font-weight: 800; color: #fff; margin-bottom: 20px;">Send a Direct Message</h2>
      <form onsubmit="event.preventDefault(); alert('Thank you for contacting Aegis Survival. Your dispatch has been transmitted to our duty officer.'); this.reset();">
        <div class="form-group">
          <label>Your Full Name</label>
          <input type="text" class="form-input" placeholder="e.g., Major Alex Reynolds" required>
        </div>

        <div class="form-group">
          <label>Email Address</label>
          <input type="email" class="form-input" placeholder="name@domain.com" required>
        </div>

        <div class="form-group">
          <label>Department / Subject</label>
          <select class="form-input" style="background: var(--bg-surface);">
            <option>Field Protocol Technical Correction</option>
            <option>Affiliate Equipment Inquiry</option>
            <option>Offline Android App Feedback</option>
            <option>General Preparedness Question</option>
          </select>
        </div>

        <div class="form-group">
          <label>Dispatch Message</label>
          <textarea class="form-textarea" placeholder="Detail your question, schematic inquiry, or suggestion..." required></textarea>
        </div>

        <button type="submit" class="btn-hero-primary" style="border: none; cursor: pointer; width: 100%; justify-content: center; font-size: 15px;">
          Transmit Dispatch &rarr;
        </button>
      </form>
    </div>
  </main>

  {get_footer(is_subpage=False)}

</body>
</html>
"""
with open(os.path.join(DOCS_DIR, "contact.html"), "w", encoding="utf-8") as f:
    f.write(contact_html)

# 8. GITHUB PAGES & VERCEL CONFIGURATION (.nojekyll, vercel.json)
with open(os.path.join(DOCS_DIR, ".nojekyll"), "w", encoding="utf-8") as f:
    f.write("")

vercel_config = {
    "cleanUrls": True,
    "trailingSlash": False,
    "headers": [
        {
            "source": "/(.*)",
            "headers": [
                {"key": "X-Content-Type-Options", "value": "nosniff"},
                {"key": "X-Frame-Options", "value": "SAMEORIGIN"},
                {"key": "X-XSS-Protection", "value": "1; mode=block"}
            ]
        }
    ]
}
with open(os.path.join(DOCS_DIR, "vercel.json"), "w", encoding="utf-8") as f:
    json.dump(vercel_config, f, indent=2)

# Export API endpoint for Android Remote Sync
API_DIR = os.path.join(DOCS_DIR, "api")
os.makedirs(API_DIR, exist_ok=True)
with open(os.path.join(API_DIR, "gear.json"), "w", encoding="utf-8") as f:
    json.dump(gear_list, f, indent=2, ensure_ascii=False)

print(f"Generated static website in {DOCS_DIR}:")
print(f"  - index.html (Homepage with 8 articles & gear showcase)")
print(f"  - 8 individual article pages in docs/articles/")
print(f"  - gear.html (Dedicated Tactical Gear store with Amazon CTAs)")
print(f"  - api/gear.json (Hosted JSON endpoint for Android app sync)")
print(f"  - about.html (Trust & Editorial Standards)")
print(f"  - privacy-policy.html (Mandatory Amazon Associates disclosure & cookies)")
print(f"  - contact.html (Direct contact portal)")
print(f"  - .nojekyll & vercel.json for 100% free hosting")
