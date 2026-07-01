# MOCKNOF — Mock Notification Firing App

<p align="center">
  <img src="app/src/main/res/drawable/ic_notification.xml" width="80" alt="MOCKNOF icon"/>
</p>

---

## 🇬🇧 English

**MOCKNOF** (Mock Notification Firing) is an Android application that lets developers and QA engineers create, configure, and fire fully customisable mock notifications on any Android device — no backend or third-party app required.

### 📱 Overview

MOCKNOF is designed for:
- **App Developers** who need to test UI behaviour when notifications arrive
- **QA / Testers** who need reproducible notification test cases in various formats
- **UI/UX Designers** who want to preview real notification appearances on a physical device

### ✨ Key Features

#### 🔧 Template Editor
Build and save notification templates with full control over every parameter:

| Parameter | Description |
|---|---|
| App Name / Package | Simulate the sender app name and package shown in the status bar |
| Channel ID / Name | Create isolated notification channels per fake app |
| Title / Body / Big Text | Notification text with optional `BigTextStyle` support |
| Importance / Priority | Set notification importance and priority levels |
| Delay / Repeat | Schedule a delay before firing and repeat multiple times |
| Progress Bar | Show determinate (0–100) or indeterminate progress |
| Actions | Add Dismiss, Inline Reply, or Custom action buttons |
| Small Icon | Choose from Email, Chat, Alert, Download, Upload, Music, Call icons |
| Vibrate / Sound | Toggle vibration and sound independently |
| Color / Group Key | Set accent colour and notification grouping |

#### 🚀 Built-in Presets
Ready-to-fire presets that mimic real apps:
- **LINE** — Chat message simulation
- **Gmail** — New email with BigText expanded view
- **YouTube** — Live stream alert
- **Grab** — Ride arrival update with Action buttons
- **Bank App** — Payment received notification
- **Download Manager** — Ongoing notification with progress bar
- **Incoming Call** — Simulated incoming call with Answer / Decline actions
- **Stress Test** — Fires 5 notifications every 2 seconds

Any preset can be **cloned** into a custom template for further editing.

#### 📋 Fire History
Every fired notification is logged with a timestamp. Stores up to 200 entries, clearable at any time.

#### ⏰ Scheduled Notifications
Delay firing via `AlarmManager` with support for exact and inexact alarms (Android 12+ exact-alarm permission handled automatically).

### 🏗️ Project Structure

```
app/src/main/
├── java/com/mocknof/app/
│   ├── model/
│   │   ├── NotificationTemplate.kt    # Core data class with ActionType & SmallIconType enums
│   │   └── NotificationHistory.kt     # Fired notification log entry
│   ├── service/
│   │   ├── NotificationAlarmReceiver.kt   # BroadcastReceiver — fires scheduled notifications
│   │   └── NotificationActionReceiver.kt  # BroadcastReceiver — handles action button taps
│   ├── ui/
│   │   ├── MainActivity.kt               # Template list screen
│   │   ├── NotificationEditorActivity.kt # Create / edit template screen
│   │   ├── PresetListActivity.kt          # Built-in preset browser
│   │   ├── HistoryActivity.kt             # Fire history screen
│   │   ├── MainViewModel.kt               # ViewModel for MainActivity
│   │   ├── EditorViewModel.kt             # ViewModel for Editor
│   │   └── adapter/
│   │       ├── TemplateAdapter.kt         # RecyclerView adapter for template list
│   │       ├── ActionEditorAdapter.kt     # Adapter for action button editor
│   │       └── HistoryAdapter.kt          # Adapter for history list
│   └── utils/
│       ├── NotificationHelper.kt   # Channel management, notification building & firing
│       └── PrefsStorage.kt         # SharedPreferences + Gson persistence layer
└── res/
    ├── layout/     # XML layouts for all screens
    ├── drawable/   # Vector icons
    └── values/     # Colors, strings, themes
```

### 🛠️ Tech Stack

| Technology | Details |
|---|---|
| Language | Kotlin |
| Min SDK | API 26 (Android 8.0 Oreo) |
| Target SDK | API 34 (Android 14) |
| Architecture | MVVM (ViewModel + LiveData) |
| UI | ViewBinding, RecyclerView, Material Design |
| Storage | SharedPreferences + Gson |
| Notifications | NotificationCompat, NotificationChannel, AlarmManager |
| Build System | Gradle (Kotlin DSL) |

### 🚀 Getting Started

**Requirements**
- Android Studio Hedgehog or newer
- JDK 17+
- Android 8.0 device or emulator (API 26+)

**Steps**

1. **Clone the repository**
   ```bash
   git clone https://github.com/mudchi/MOCKNOF.git
   cd MOCKNOF
   ```

2. **Open in Android Studio**
   - File → Open → select the `MOCKNOF` folder
   - Wait for Gradle sync to complete

3. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or press **Run ▶** in Android Studio.

4. **Grant permission**
   - On Android 13+, the app will request `POST_NOTIFICATIONS` on first launch.

### 📖 How to Use

1. **New Template** — Tap the **＋** FAB (bottom-right corner)
2. **Fill in the editor** — configure any parameters, then tap **Save** or **Save & Fire**
3. **Fire a notification** — tap the **▶ Fire** button on any template card
4. **Use Presets** — Menu → **Presets** to browse built-in templates
5. **View History** — Menu → **History** to see all previously fired notifications
6. **Cancel all** — Menu → **Cancel All** to dismiss every active notification

### 📦 Download APK

A pre-built debug APK is available in the repository root: [`MOCKNOF-debug.apk`](MOCKNOF-debug.apk)

> ⚠️ Enable **"Install unknown apps"** in Android settings before installing.

---

## 🇹🇭 ภาษาไทย

**MOCKNOF** (Mock Notification Firing) เป็นแอปพลิเคชัน Android ที่ช่วยให้นักพัฒนาและ QA Engineer สามารถสร้าง ปรับแต่ง และยิง notification จำลองบนอุปกรณ์ Android ได้อย่างละเอียดและยืดหยุ่น โดยไม่ต้องพึ่งพาแอปอื่น

### 📱 ภาพรวมแอปพลิเคชัน

MOCKNOF ถูกออกแบบมาสำหรับ:
- **นักพัฒนาแอป** ที่ต้องการทดสอบ UI เมื่อมี notification เข้ามา
- **QA / Tester** ที่ต้องการสร้าง test case สำหรับ notification ในรูปแบบต่างๆ
- **UI/UX Designer** ที่ต้องการดูหน้าตาของ notification บนอุปกรณ์จริง

### ✨ ฟีเจอร์หลัก

#### 🔧 Template Editor (ตัวแก้ไข Template)
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

#### 🚀 Built-in Presets
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

#### 📋 Fire History (ประวัติการยิง)
บันทึก notification ทุกรายการที่ถูกยิงพร้อม timestamp เก็บได้สูงสุด 200 รายการ

#### ⏰ Scheduled Notification
ตั้งหน่วงเวลา (delay) ก่อนยิง notification ผ่าน `AlarmManager` รองรับทั้ง exact alarm และ inexact alarm

### 🏗️ โครงสร้างโปรเจ็ค

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

### 🛠️ Tech Stack

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

### 🚀 วิธีติดตั้งและใช้งาน

**ความต้องการของระบบ**
- Android Studio Hedgehog หรือใหม่กว่า
- JDK 17+
- อุปกรณ์หรือ Emulator Android 8.0 (API 26) ขึ้นไป

**ขั้นตอนการติดตั้ง**

1. **Clone โปรเจ็ค**
   ```bash
   git clone https://github.com/mudchi/MOCKNOF.git
   cd MOCKNOF
   ```

2. **เปิดด้วย Android Studio**
   - เปิด Android Studio → Open → เลือกโฟลเดอร์ `MOCKNOF`
   - รอให้ Gradle sync เสร็จ

3. **Build และ Run**
   ```bash
   ./gradlew assembleDebug
   ```
   หรือกด **Run ▶** ใน Android Studio

4. **อนุญาต Permission**
   - บนอุปกรณ์ Android 13+ แอปจะขอ permission `POST_NOTIFICATIONS` เมื่อเปิดครั้งแรก

### 📖 วิธีใช้งาน

1. **สร้าง Template ใหม่** — กดปุ่ม **＋** (FAB) ที่มุมขวาล่าง
2. **กรอกข้อมูล** ใน Editor แล้วกด **Save** หรือ **Save & Fire**
3. **ยิง Notification** — กดปุ่ม **▶ Fire** ที่การ์ด template
4. **ใช้ Presets** — เข้าเมนู → **Presets** เพื่อดู template สำเร็จรูป
5. **ดูประวัติ** — เข้าเมนู → **History** เพื่อดูรายการที่ยิงไปแล้ว
6. **ยกเลิกทั้งหมด** — เข้าเมนู → **Cancel All** เพื่อลบ notification ออกจาก shade

### 📦 ดาวน์โหลด APK

ไฟล์ APK พร้อมใช้งานอยู่ที่ root ของ repository: [`MOCKNOF-debug.apk`](MOCKNOF-debug.apk)

> ⚠️ ต้องเปิด **"ติดตั้งแอปจากแหล่งที่ไม่รู้จัก"** ในการตั้งค่า Android ก่อนติดตั้ง

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
