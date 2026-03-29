package main

import (
	"context"
	"log"
	"net/http"
	"os"

	myws "realtime-service/internal/websocket"

	"github.com/gorilla/websocket"
	"github.com/joho/godotenv" // THÊM THƯ VIỆN NÀY
	"github.com/redis/go-redis/v9"
)

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin:     func(r *http.Request) bool { return true },
}

func main() {
	// 1. NẠP FILE .ENV NGAY LÚC KHỞI ĐỘNG
	// Lưu ý tư duy Production: Chỉ in ra cảnh báo chứ không dùng log.Fatal làm sập Server.
	// Vì khi đưa lên Docker thực tế, ta không dùng file .env nữa mà truyền thẳng biến qua docker-compose.
	if err := godotenv.Load(); err != nil {
		log.Println("⚠️ Không tìm thấy file .env, đang sử dụng biến môi trường của hệ thống (OS Environment).")
	}

	// 2. Lấy port từ .env (Nếu không có thì mặc định là 8080)
	port := os.Getenv("SERVER_PORT")
	if port == "" {
		port = "3004"
	}

	// Thêm đoạn này vào trên dòng 35
	redisAddr := os.Getenv("REDIS_ADDR")
	if redisAddr == "" {
		redisAddr = "localhost:6379"
	}

	// Bây giờ dòng 35-37 sẽ chạy ngon
	rdb := redis.NewClient(&redis.Options{
		Addr: redisAddr,
	})
	manager := myws.NewManager(rdb)
	go manager.Run()

	// Khởi chạy lính gác kho Redis
	go listenRedis(manager)

	http.HandleFunc("/ws", func(w http.ResponseWriter, r *http.Request) {
		serveWs(manager, w, r)
	})

	log.Printf("🚀 Trạm điều phối đã mở cửa tại cổng :%s...", port)
	log.Fatal(http.ListenAndServe(":"+port, nil)) // Dùng biến port ở đây
}

func listenRedis(manager *myws.Manager) {
	ctx := context.Background()

	redisAddr := os.Getenv("REDIS_ADDR")
	if redisAddr == "" {
		redisAddr = "localhost:6379"
	}

	rdb := redis.NewClient(&redis.Options{
		Addr: redisAddr,
	})

	// 1. Dùng PSubscribe để nghe tất cả các kênh có chữ "channel:"
	pattern := "ChatService_channel:*"
	pubsub := rdb.PSubscribe(ctx, pattern)
	defer pubsub.Close()

	log.Printf("🎧 Đã kết nối Redis (%s). Đang nghe tất cả kênh dạng: '%s'...", redisAddr, pattern)

	// 2. Dùng biến để nhận tin nhắn từ Pattern Subscribe
	for {
		msg, err := pubsub.ReceiveMessage(ctx)
		if err != nil {
			log.Printf("❌ Lỗi khi nhận tin Redis: %v", err)
			return
		}

		log.Printf("📥 Nhận tin từ Redis [%s]: %s", msg.Channel, msg.Payload)

		// 3. Đẩy vào Manager để thổi tới các máy của người dùng
		manager.Broadcast <- []byte(msg.Payload)
	}
}

// ... (Hàm serveWs giữ nguyên như cũ) ...
func serveWs(manager *myws.Manager, w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println("Lỗi khi cắm ống:", err)
		return
	}

	userId := r.URL.Query().Get("userId")
	channelId := r.URL.Query().Get("channelId")
	if userId == "" || channelId == "" {
		log.Println("Thiếu userId hoặc channelId")
		return
	}
	if userId == "" {
		userId = "khach_an_danh"
	}

	client := myws.NewClient(userId, channelId, conn, manager)
	manager.Register <- client

	go client.WritePump()
	go client.ReadPump()
}
