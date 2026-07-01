# MOCKNOF — Mock Notification Firing App

<p align="center">
  <img src="app/src/main/res/drawable/ic_notification.xml" width="80" alt="MOCKNOF icon"/>
</p>

**MOCKNOF** (Mock Notification Firing) เป็นแอปพลิเคชัน Android ที่ช่วยให้นักพัฒนาและ QA Engineer สามารถสร้าง ปรับแต่ง และยิง notification จำลองบนอุปกรณ์ Android ได้อย่างละเอียดและยืดหยุ่น โดยไม่ต้องพึ่งพาแอปอื่น

---

## 📱 ภาพรวมแอปพลิเคชัน

MOCKNOF ถูกออกแบบมาสำหรับ:
- **นักพัฒนาแอป** ที่ต้องการทดสอบ UI เมื่อมี notification เข้ามา
- **QA / Tester** ที่ต้องการสร้าง test case สำหรับ notification ในรูปแบบต่างๆ
- **UI/UX Designer** ที่ต้องการดูหน้าตาของ notification บนอุปกรณ์จริง

---

## ✨ ฟีเจอร์หลัก

### 🔧 Template Editor (ตัวแก้ไข Template)
สร้างและบันทึก notification template ได้อย่างละเอียด ปรับแต่งได้ทุกพารามิเตอร์ เช่น:

| พารามิเตอร์ | คำอธิบาย |
|---|---|
| App Name / Package | จำลองชื่อแอปและ package ที่ส่ง notification |
| Channel ID / Name | กำหนด notification channel แยกตามแต่ละ "แอปจำลอง" |
| Title / Body / Big Text | ข้อความใน notification รองรับรูปแบบ BigTextStyle |
| Importance / Priority | ระดับความสำคัญของ notification |
| Delay / Repeat | ตั้งเวลาหน่วง และจำนวนครั้งที่ยิงซ้ำ |
| Progress Bar | แสดง progress bar แบบ determinate หรือ indeterminate |
| Actions | เพิ่มปุ่ม Action ได้ทั้งแบบ Dismiss, Reply (Inline), และ Custom |
| Small Icon | เลือก icon ประเภทต่างๆ เช่น Email, Chat, Call, Download ฯลฯ |
| Vibrate / Sound | เปิด/ปิดเสียงและการสั่น |
| Color / Group Key | กำหนดสีเน้นและการจัดกลุ่ม notification |

### 🚀 Built-in Presets
มี preset สำเร็จรูปให้ใช้งานทันที เช่น:
- **LINE** — ข้อความแชทจำลอง
- **Gmail** — อีเมลใหม่ พร้อม BigText
- **YouTube** — แจ้งเตือน Live stream
- **Grab** — อัปเดตสถานะการรับรถ พร้อมปุ่ม Action
- **Bank App** — แจ้งเตือนรับเงิน
- **Download Manager** — notification แบบ ongoing พร้อม progress bar
- **Incoming Call** — โทรเข้าจำลอง
- **Stress Test** — ยิง notification ซ้ำ 5 ครั้งทุก 2 วินาที เพื่อทดสอบระบบ

สามารถ **Clone** preset มาแก้ไขเป็น custom template ของตัวเองได้

### 📋 Fire History (ประวัติการยิง)
บันทึก notification ทุกรายการที่ถูกยิงพร้อม timestamp เก็บได้สูงสุด 200 รายการ

### ⏰ Scheduled Notification
ตั้งหน่วงเวลา (delay) ก่อนยิง notification ผ่าน `AlarmManager` รองรับทั้ง exact alarm และ inexact alarm

---

## 🏗️ โครงสร้างโปรเจ็ค

```
app/src/main/
├── java/com/mocknof/app/
│   ├── model/
│   │   ├── NotificationTemplate.kt   # data class หลัก พร้อม enum ActionType, SmallIconType
│   │   └── NotificationHistory.kt    # บันทึกประวัติการยิง notification
│   ├── service/
│   │   ├── NotificationAlarmReceiver.kt   # BroadcastReceiver รับ alarm แล้วยิง notification
│   │   └── NotificationActionReceiver.kt  # BroadcastReceiver จัดการ Action button
│   ├── ui/
│   │   ├── MainActivity.kt              # หน้าหลัก แสดงรายการ template
│   │   ├── NotificationEditorActivity.kt # หน้าสร้าง/แก้ไข template
│   │   ├── PresetListActivity.kt         # หน้า built-in presets
│   │   ├── HistoryActivity.kt            # หน้าประวัติการยิง
│   │   ├── MainViewModel.kt              # ViewModel สำหรับ MainActivity
│   │   ├── EditorViewModel.kt            # ViewModel สำหรับ Editor
│   │   └── adapter/
│   │       ├── TemplateAdapter.kt        # RecyclerView adapter สำหรับ template list
│   │       ├── ActionEditorAdapter.kt    # adapter สำหรับแก้ไข action buttons
│   │       └── HistoryAdapter.kt         # adapter สำหรับประวัติ
│   └── utils/
│       ├── NotificationHelper.kt   # สร้าง channel, build และยิง notification จริง
│       └── PrefsStorage.kt         # บันทึก/โหลด template และ history ด้วย SharedPreferences + Gson
└── res/
    ├── layout/     # XML layout ทุกหน้า
    ├── drawable/   # icon ประเภทต่างๆ
    └── values/     # colors, strings, themes
```

---

## 🛠️ Tech Stack

| เทคโนโลยี | รายละเอียด |
|---|---|
| Language | Kotlin |
| Min SDK | API 26 (Android 8.0 Oreo) |
| Target SDK | API 34 (Android 14) |
| Architecture | MVVM (ViewModel + LiveData) |
| UI | ViewBinding, RecyclerView, Material Design |
| Storage | SharedPreferences + Gson |
| Notification | NotificationCompat, NotificationChannel, AlarmManager |
| Build System | Gradle (Kotlin DSL) |

---

## 🚀 วิธีติดตั้งและใช้งาน

### ความต้องการของระบบ
- Android Studio Hedgehog หรือใหม่กว่า
- JDK 8+
- อุปกรณ์หรือ Emulator Android 8.0 (API 26) ขึ้นไป

### ขั้นตอนการติดตั้ง

1. **Clone โปรเจ็ค**
   ```bash
   git clone https://github.com/mudchi/MOCKNOF.git
   cd MOCKNOF
   ```

2. **เปิดด้วย Android Studio**
   - เปิด Android Studio → Open → เลือกโฟลเดอร์ `MOCKNOF`
   - รอให้ Gradle sync เสร็จ

3. **Build และ Run**
   - กด **Run ▶** หรือใช้คำสั่ง:
   ```bash
   ./gradlew assembleDebug
   ```

4. **อนุญาต Permission**
   - บนอุปกรณ์ Android 13+ แอปจะขอ permission `POST_NOTIFICATIONS` เมื่อเปิดครั้งแรก

---

## 📖 วิธีใช้งาน

1. **สร้าง Template ใหม่** — กดปุ่ม ＋ (FAB) ที่มุมขวาล่าง
2. **กรอกข้อมูล** ใน Editor แล้วกด **Save** หรือ **Save & Fire**
3. **ยิง Notification** — กดปุ่ม Fire ▶ ที่การ์ด template
4. **ใช้ Presets** — เข้าเมนู → Presets เพื่อดู template สำเร็จรูป
5. **ดูประวัติ** — เข้าเมนู → History เพื่อดูรายการที่ยิงไปแล้ว
6. **ยกเลิกทั้งหมด** — เข้าเมนู → Cancel All เพื่อลบ notification ออกจาก shade

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
