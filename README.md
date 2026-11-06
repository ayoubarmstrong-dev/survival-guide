# Antigravity Survival & Preparedness Mobile Application
## Architecture & Offline Database Package (v1.0.0)

### 1. محتويات الحزمة (Package Contents):
* **/database/survival_master.db**: قاعدة بيانات SQLite كاملة جاهزة للوضع المباشر في مجلد `assets/` لتطبيق الأندرويد.
* **/json_modules/**: ملفات JSON مهيكلة للربط المباشر أو التحديث الهجين:
  * `categories.json`: الأقسام الخمسة الرئيسية.
  * `articles.json`: المقالات الميدانية التفصيلية المدعمة بـ Markdown.
  * `checklists.json`: قوائم الفحص والتحقق التفاعلية (Checklists).
  * `affiliate_gear.json`: العتاد الموصى به للشراء مع روابط التابع التقديرية.
  * `ration_formulas.json`: معادلات حساب المؤن والمياه اليومية للمحرك البرمجي.
* **/svg_diagrams/**: رسوم بيانية وتوضيحية فيكتور خفيفة الوزن وعالية الدقة (Vector SVG) مخصصة للشاشات الداكنة (AMOLED Black):
  * `water_filtration.svg`: نظام ترشيح المياه الطبقي (Bio-Sand).
  * `tourniquet_application.svg`: خطوات تطبيق عاصبة النزيف التكتيكية المعتمدة.
  * `nuclear_fallout_shelter.svg`: معاملات الوقاية وحجب الإشعاع حسب نوع المأوى.

### 2. إرشادات التكامل في Antigravity / Android:
1. انسخ ملف `survival_master.db` إلى مسار الأصول: `android/app/src/main/assets/databases/`.
2. استخدم مكتبة SQLite أو Room مع خاصية `createFromAsset("databases/survival_master.db")`.
3. لا تتطلب الشاشات أي اتصال بالإنترنت لعرض المقالات والرسوم التوضيحية وحساب المؤن.
4. تفتح روابط الشراء عبر `CustomTabsIntent` أو المتصفح الخارجي عند نقر المستخدم وتوفر اتصال بالشبكة، وتظل مواصفات القطع محفوظة محلياً في حال انقطاع النت.
