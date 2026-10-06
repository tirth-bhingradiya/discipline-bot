# 🤖 DisciplineBot — Your AI Self-Management Coach

> **No Excuses. Just Execution. Every Single Day.**

A **Spring Boot** personal AI discipline coach that sends **WhatsApp reminders** to keep you accountable for your daily habits — completely **FREE**, using only free APIs.

---

## 🎯 What It Does

DisciplineBot is your **strict AI coach** that:
- 📱 Sends **WhatsApp messages** directly to your phone (Green API — Developer Free Tier)
- 🤖 Uses **Google Gemini AI** to generate harsh, no-nonsense motivational messages
- ⏰ Runs **scheduled reminders** for all 4 daily habits at optimal times (IST)
- 📊 Sends a **nightly performance report** with your grade (S/A/B/C/F)
- 🌐 Has a **dark-mode premium dashboard** at `http://localhost:8080`
- 🔥 Tracks **daily streaks** to keep you consistent

---

## 📋 Your Daily Habits

| Habit | Times (IST) | Goal |
|---|---|---|
| 💧 Drink Water | 8AM, 11AM, 2PM, 5PM, 8PM | 3 Liters/day |
| 🧴 Skin Care | 7AM + 9PM | Morning & Night routine |
| 🙏 Chanting | 6AM + 7PM | Twice daily |
| 💊 Hair Medicine | 8AM | Once daily with breakfast |
| ⚡ Morning Kickstart | 5:45AM | Daily mission briefing |
| 📊 Evening Summary | 9:30PM | Full accountability report |

---

## ⚡ Quick Setup (5 Minutes)

### Step 1 — Get Green API Credentials (FREE)
1. Go to [Green API](https://greenapi.com/en/) and create a free account.
2. Create a **Developer** instance (Free tier gives 3,000 messages/month).
3. Scan the provided QR code with your WhatsApp app on your phone (Linked Devices).
4. Copy your **Instance ID** and **API Token** from the console.

### Step 2 — Get Gemini API Key (FREE)
1. Go to: **https://aistudio.google.com/apikey**
2. Click "Create API Key"
3. Copy the key

### Step 3 — Configure the App
Edit [`src/main/resources/application.properties`](src/main/resources/application.properties):
```properties
whatsapp.greenapi.instance-id=YOUR_INSTANCE_ID
whatsapp.greenapi.api-token=YOUR_API_TOKEN
whatsapp.target-phone=YOUR_PHONE_NUMBER_WITH_COUNTRY_CODE
gemini.api.key=YOUR_GEMINI_KEY
```

### Step 4 — Run It!
```bash
# Windows
mvn spring-boot:run

# OR build and run JAR
mvn clean package -DskipTests
java -jar target/discipline-bot-1.0.0.jar
```

Open dashboard: **http://localhost:8080**

---

## 🏗️ Architecture

```
Spring Boot App (localhost:8080)
├── NotificationScheduler — Cron jobs firing at exact IST times
├── GeminiAIService — Generates strict AI messages (free API)
├── WhatsAppBotService — Sends WhatsApp messages (Green API)
├── HabitService — Tracks completions, streaks, summaries
└── Dashboard (Thymeleaf) — Beautiful dark-mode web UI
```

---

## 📱 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Main dashboard |
| GET | `/summary` | Daily summary page |
| POST | `/habit/done?habitId=1` | Mark habit complete |
| POST | `/api/test/whatsapp` | Test WhatsApp connection |
| POST | `/api/send/summary` | Send summary now |
| GET | `/api/today` | Today's status (JSON) |
| GET | `/h2-console` | Database browser |

---

## 💰 Cost Breakdown

| Service | Free Tier | What You Use |
|---|---|---|
| Green API (WhatsApp) | 3,000 msgs/month | ~15 messages/day |
| Google Gemini API | 1M tokens/month | ~500 tokens/day |
| H2 Database | Unlimited | Local storage |
| **Total** | **$0.00** | **Forever free** |

---

## 🛠️ Tech Stack
- **Java 17** + **Spring Boot 3.2**
- **Spring Scheduler** (cron jobs)
- **H2 Database** (file persistence)
- **Thymeleaf** (web templates)
- **WebFlux WebClient** (HTTP calls)
- **Lombok** (clean code)

---

## 🆘 Troubleshooting

**WhatsApp not sending?**
- Ensure you scanned the QR code in the Green API console.
- Check your target phone number format (must include country code, e.g., 919876543210).
- Test via dashboard: click "Test WhatsApp"

**No AI messages?**
- Check your Gemini API key is valid
- App automatically uses pre-written fallback messages if AI fails

**Reminders not firing?**
- Your PC must be ON and the app running at reminder times
- Check logs: `logging.level.com.disciplinebot=DEBUG`

---

> 🤖 *DisciplineBot is watching. No excuses. Just execution.*
