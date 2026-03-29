package redis

import (
	"context"
	"log"

	// 1. Đặt bí danh (alias) là rdb để không đụng hàng với tên package "redis" của bạn
	rdb "github.com/redis/go-redis/v9"

	// Nhớ sửa lại đường dẫn này cho khớp với dự án của bạn
	myws "realtime-service/internal/websocket"
)

// 2. PHẦN BẠN CÒN THIẾU: Định nghĩa cấu trúc của Subscriber
type Subscriber struct {
	client  *rdb.Client
	manager *myws.Manager
}

// 3. Hàm Constructor: Dùng để khởi tạo một Subscriber mới
func NewSubscriber(client *rdb.Client, manager *myws.Manager) *Subscriber {
	return &Subscriber{
		client:  client,
		manager: manager,
	}
}
func (s *Subscriber) Subscribe(ctx context.Context) {
	// 👈 SỬA Ở ĐÂY: Nghe tất cả các kênh bắt đầu bằng "channel:"
	pattern := "channel:*"
	pubsub := s.client.PSubscribe(ctx, pattern)
	defer pubsub.Close()

	log.Printf("🎧 Đang nghe Redis với pattern: '%s'...", pattern)

	for {
		msg, err := pubsub.ReceiveMessage(ctx)
		if err != nil {
			log.Println("❌ Lỗi Redis:", err)
			return
		}

		log.Printf("📥 Nhận tin từ Redis [%s]: %s", msg.Channel, msg.Payload)

		// Đẩy tin nhắn vào Manager để tỏa đi cho các Client
		s.manager.Broadcast <- []byte(msg.Payload)
	}
}
