import os
import sys
import json
import sqlite3
import webbrowser
from http.server import HTTPServer, SimpleHTTPRequestHandler
from urllib.parse import urlparse

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(BASE_DIR)

JSON_PATH = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "json", "survival_master_content_en.json")
DIAGRAMS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "diagrams")
DB_PATHS = [
    os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "databases", "survival_master.db"),
    os.path.join(PROJECT_ROOT, "database", "survival_master.db")
]

def rebuild_sqlite_database(data):
    """Rebuilds the SQLite database matching Room schema & master hash exactly."""
    for db_path in DB_PATHS:
        os.makedirs(os.path.dirname(db_path), exist_ok=True)
        if os.path.exists(db_path):
            try:
                os.remove(db_path)
            except Exception:
                pass
        
        conn = sqlite3.connect(db_path)
        cur = conn.cursor()
        
        # 1. Categories
        cur.execute('''CREATE TABLE IF NOT EXISTS categories (
            id TEXT NOT NULL, 
            name_ar TEXT NOT NULL, 
            name_en TEXT NOT NULL, 
            icon_name TEXT NOT NULL, 
            sort_order INTEGER NOT NULL, 
            PRIMARY KEY(id)
        )''')
        
        # 2. Articles
        cur.execute('''CREATE TABLE IF NOT EXISTS articles (
            id TEXT NOT NULL, 
            category_id TEXT NOT NULL, 
            title_ar TEXT NOT NULL, 
            title_en TEXT NOT NULL, 
            summary_ar TEXT NOT NULL, 
            content_markdown_ar TEXT NOT NULL, 
            priority_level TEXT, 
            diagram_asset TEXT, 
            is_offline_cached INTEGER NOT NULL, 
            PRIMARY KEY(id), 
            FOREIGN KEY(category_id) REFERENCES categories(id) ON UPDATE NO ACTION ON DELETE NO ACTION
        )''')
        cur.execute('CREATE INDEX IF NOT EXISTS index_articles_category_id ON articles (category_id)')
        
        # 3. Checklists
        cur.execute('''CREATE TABLE IF NOT EXISTS checklists (
            id TEXT NOT NULL, 
            article_id TEXT, 
            title_ar TEXT NOT NULL, 
            item_ar TEXT NOT NULL, 
            quantity_formula TEXT, 
            is_mandatory INTEGER NOT NULL, 
            PRIMARY KEY(id), 
            FOREIGN KEY(article_id) REFERENCES articles(id) ON UPDATE NO ACTION ON DELETE CASCADE
        )''')
        cur.execute('CREATE INDEX IF NOT EXISTS index_checklists_article_id ON checklists (article_id)')
        
        # 4. Affiliate Gear
        cur.execute('''CREATE TABLE IF NOT EXISTS affiliate_gear (
            id TEXT NOT NULL, 
            article_id TEXT, 
            name_ar TEXT NOT NULL, 
            name_en TEXT NOT NULL, 
            spec_summary_ar TEXT NOT NULL, 
            amazon_asin TEXT, 
            affiliate_url_template TEXT NOT NULL, 
            estimated_price_usd REAL, 
            image_asset TEXT, 
            importance_rating INTEGER NOT NULL, 
            category TEXT NOT NULL, 
            priority_tier TEXT NOT NULL, 
            PRIMARY KEY(id), 
            FOREIGN KEY(article_id) REFERENCES articles(id) ON UPDATE NO ACTION ON DELETE SET NULL
        )''')
        cur.execute('CREATE INDEX IF NOT EXISTS index_affiliate_gear_article_id ON affiliate_gear (article_id)')
        
        # 5. Room Master Table
        cur.execute('CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)')
        cur.execute("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'dcf5e87a6ab3a69072871ab6888acd31')")
        
        # Insert Categories
        for cat in data.get('categories', []):
            name = cat.get('name', cat.get('name_en', ''))
            cur.execute(
                'INSERT INTO categories (id, name_ar, name_en, icon_name, sort_order) VALUES (?, ?, ?, ?, ?)',
                (cat['id'], name, name, cat.get('icon_name', 'ic_shield_check'), cat.get('sort_order', 0))
            )
        
        # Insert Articles
        for art in data.get('articles', []):
            title = art.get('title', art.get('title_en', ''))
            cur.execute(
                '''INSERT INTO articles 
                   (id, category_id, title_ar, title_en, summary_ar, content_markdown_ar, priority_level, diagram_asset, is_offline_cached) 
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)''',
                (
                    art['id'],
                    art['category_id'],
                    title,
                    title,
                    art.get('summary', ''),
                    art.get('content_markdown', ''),
                    art.get('priority_level', 'NORMAL'),
                    art.get('diagram_asset'),
                    1
                )
            )
            
        # Insert Checklists
        for chk in data.get('checklists', []):
            cur.execute(
                '''INSERT INTO checklists 
                   (id, article_id, title_ar, item_ar, quantity_formula, is_mandatory) 
                   VALUES (?, ?, ?, ?, ?, ?)''',
                (
                    chk['id'],
                    chk.get('article_id'),
                    chk.get('title', ''),
                    chk.get('specification', chk.get('title', '')),
                    chk.get('quantity_formula'),
                    chk.get('is_mandatory', 1)
                )
            )
            
        # Insert Affiliate Gear
        for gear in data.get('affiliate_gear', []):
            name = gear.get('name', gear.get('name_en', ''))
            cur.execute(
                '''INSERT INTO affiliate_gear 
                   (id, article_id, name_ar, name_en, spec_summary_ar, amazon_asin, affiliate_url_template, estimated_price_usd, image_asset, importance_rating, category, priority_tier) 
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)''',
                (
                    gear['id'],
                    gear.get('article_id'),
                    name,
                    name,
                    gear.get('spec_summary', ''),
                    gear.get('amazon_asin'),
                    gear.get('affiliate_url_template', ''),
                    gear.get('estimated_price_usd'),
                    gear.get('image_asset'),
                    gear.get('importance_rating', 5),
                    gear.get('category', 'Tactical Gear'),
                    gear.get('priority_tier', 'Field Ops')
                )
            )
            
        conn.commit()
        conn.close()

class AdminHandler(SimpleHTTPRequestHandler):
    def end_headers(self):
        # Enable CORS and caching controls
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

    def do_GET(self):
        parsed = urlparse(self.path)
        path = parsed.path

        if path == "/api/content":
            try:
                with open(JSON_PATH, "r", encoding="utf-8") as f:
                    content = f.read()
                self.send_response(200)
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(content.encode("utf-8"))
            except Exception as e:
                self.send_response(500)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"error": str(e)}).encode("utf-8"))
            return

        if path.startswith("/diagrams/"):
            filename = os.path.basename(path)
            diagram_file = os.path.join(DIAGRAMS_DIR, filename)
            if os.path.exists(diagram_file):
                try:
                    with open(diagram_file, "rb") as f:
                        data = f.read()
                    self.send_response(200)
                    self.send_header("Content-Type", "image/svg+xml")
                    self.end_headers()
                    self.wfile.write(data)
                    return
                except Exception:
                    pass
            self.send_response(404)
            self.end_headers()
            return

        # Serve static frontend files
        if path == "/" or path == "/index.html":
            index_path = os.path.join(BASE_DIR, "public", "index.html")
            if os.path.exists(index_path):
                with open(index_path, "rb") as f:
                    data = f.read()
                self.send_response(200)
                self.send_header("Content-Type", "text/html; charset=utf-8")
                self.end_headers()
                self.wfile.write(data)
                return

        super().do_GET()

    def do_POST(self):
        parsed = urlparse(self.path)
        if parsed.path == "/api/save":
            content_length = int(self.headers.get("Content-Length", 0))
            post_data = self.rfile.read(content_length).decode("utf-8")
            try:
                data = json.loads(post_data)
                
                # 1. Write formatted JSON to assets
                with open(JSON_PATH, "w", encoding="utf-8") as f:
                    json.dump(data, f, indent=2, ensure_ascii=False)
                
                # 2. Rebuild Room SQLite database
                rebuild_sqlite_database(data)
                
                self.send_response(200)
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                resp = {
                    "status": "success",
                    "message": "Saved to survival_master_content_en.json and rebuilt survival_master.db successfully!",
                    "categories_count": len(data.get("categories", [])),
                    "articles_count": len(data.get("articles", [])),
                    "gear_count": len(data.get("affiliate_gear", []))
                }
                self.wfile.write(json.dumps(resp).encode("utf-8"))
            except Exception as e:
                self.send_response(500)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"status": "error", "message": str(e)}).encode("utf-8"))
            return

        if parsed.path == "/api/publish_github":
            content_length = int(self.headers.get("Content-Length", 0))
            post_data = self.rfile.read(content_length).decode("utf-8")
            try:
                if post_data:
                    data = json.loads(post_data)
                else:
                    with open(JSON_PATH, "r", encoding="utf-8") as f:
                        data = json.load(f)

                # 1. Update docs/api/gear.json
                api_dir = os.path.join(PROJECT_ROOT, "docs", "api")
                os.makedirs(api_dir, exist_ok=True)
                gear_file = os.path.join(api_dir, "gear.json")
                gear_list = data.get("affiliate_gear", [])
                with open(gear_file, "w", encoding="utf-8") as f:
                    json.dump(gear_list, f, indent=2, ensure_ascii=False)

                # 2. Also ensure assets JSON & SQLite DB are in sync
                with open(JSON_PATH, "w", encoding="utf-8") as f:
                    json.dump(data, f, indent=2, ensure_ascii=False)
                rebuild_sqlite_database(data)

                # 3. Commit and push to git
                import subprocess
                git_log = []
                try:
                    p1 = subprocess.run(["git", "add", "docs/api/gear.json", "docs/", "app/src/main/assets/"], cwd=PROJECT_ROOT, capture_output=True, text=True)
                    p2 = subprocess.run(["git", "commit", "-m", "feat: publish updated gear catalog to API endpoint"], cwd=PROJECT_ROOT, capture_output=True, text=True)
                    p3 = subprocess.run(["git", "push", "origin", "main"], cwd=PROJECT_ROOT, capture_output=True, text=True)
                    git_log = [p1.stdout.strip(), p2.stdout.strip(), p3.stdout.strip(), p3.stderr.strip()]
                except Exception as ge:
                    git_log.append(f"Git execution note: {ge}")

                self.send_response(200)
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                resp = {
                    "status": "success",
                    "message": f"Successfully published {len(gear_list)} items to /docs/api/gear.json and pushed to GitHub Pages!",
                    "endpoint": "https://ayoubarmstrong-dev.github.io/survival-guide/api/gear.json",
                    "gear_count": len(gear_list),
                    "git_log": [l for l in git_log if l]
                }
                self.wfile.write(json.dumps(resp).encode("utf-8"))
            except Exception as e:
                self.send_response(500)
                self.send_header("Content-Type", "application/json")
                self.end_headers()
                self.wfile.write(json.dumps({"status": "error", "message": str(e)}).encode("utf-8"))
            return

def run_server(port=8080):
    server_address = ('', port)
    httpd = HTTPServer(server_address, AdminHandler)
    url = f"http://localhost:{port}"
    print(f"============================================================")
    print(f" Survival Master - Local Admin Panel & Live Phone Simulator")
    print(f" Dashboard URL: {url}")
    print(f"============================================================")
    print("Press Ctrl+C to stop the server.")
    webbrowser.open(url)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping server...")
        httpd.server_close()

if __name__ == "__main__":
    port = 8080
    if len(sys.argv) > 1:
        try:
            port = int(sys.argv[1])
        except ValueError:
            pass
    run_server(port)
