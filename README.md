<div align="center">

# 📍 UNLOST - Smart Item Tracker

**Đừng để mất đồ thêm một lần nào nữa.**  
*Ứng dụng Android ghi nhớ và tìm kiếm vị trí đồ đạc thông minh qua giọng nói & văn bản (Offline-First).*

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API_26+-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg)](#-architecture)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

---

## 📌 Bối cảnh & Mục tiêu (Problem Statement)
Mọi người thường xuyên quên mất mình đã để chìa khóa, ví, giấy tờ hay đồ dùng cá nhân ở đâu. **Unlost** ra đời nhằm giải quyết vấn đề này theo cách tự nhiên nhất:
- **Ghi nhớ bằng giọng nói:** Chỉ cần nói *"Tôi để chìa khóa trên bàn làm việc tầng 2"*, app sẽ tự động phân tích và lưu trữ.
- **Tìm kiếm tức thì:** Hỏi *"Chìa khóa ở đâu?"*, app sẽ hiển thị ngay vị trí chính xác.
- **Offline-First & Bảo mật:** Dữ liệu lưu trữ hoàn toàn dưới thiết bị local, không lo lộ thông tin riêng tư.

---

## ✨ Tính năng chính (Key Features)

- 🎙️ **Speech-to-Text Tiếng Việt:** Nhập liệu vị trí đồ vật bằng giọng nói nhanh chóng.
- 🧩 **Smart Natural Language Parser:** Tự động bóc tách tên đồ vật & vị trí từ câu nói/văn bản tiếng Việt.
- 🔍 **Tìm kiếm thông minh:** Tìm kiếm vị trí đồ đạc nhanh chóng theo từ khóa.
- 📦 **Offline-First:** Hoạt động 100% mượt mà ngay cả khi không có kết nối Internet.
- 🎨 **Giao diện Material 3:** Modern, tối giản, hỗ trợ Dark Mode chuẩn Google.

---

## 🛠 Tech Stack & Công nghệ sử dụng

| Tầng / Thành phần | Công nghệ / Thư viện |
| :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) |
| **Architecture** | Clean Architecture + MVVM |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) |
| **Dependency Injection** | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) |
| **Asynchronous** | Kotlin Coroutines & StateFlow |
| **Navigation** | Navigation Compose |

---

## 🏗 Kiến trúc dự án (Architecture)

Dự án áp dụng mô hình **Clean Architecture** kết hợp với **MVVM** chia làm 3 lớp rõ ràng:

```text
com.dev.unlost/
│
├── data/              # Data Layer (Room DB, Entities, DAO, Repository Impl)
├── domain/            # Domain Layer (UseCases, Domain Models, Repository Interfaces)
├── ui/                # UI Layer (Compose Screens, ViewModels, Components, Theme)
├── di/                # Dependency Injection (Hilt Modules)
└── util/              # Helpers (SpeechRecognizer, Vietnamese Text Parser)
