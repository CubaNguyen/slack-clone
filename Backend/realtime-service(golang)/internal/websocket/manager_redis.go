package websocket

import (
	"context"
	"encoding/json"
	"time"
)

// Ghi trạng thái vào Redis với hạn sử dụng 5 phút để chống "Online ảo"
func (m *Manager) updatePresence(userId string, isOnline bool) {
	ctx := context.Background()
	key := "presence:" + userId

	if isOnline {
		m.RedisClient.Set(ctx, key, "online", 5*time.Minute)
	} else {
		m.RedisClient.Del(ctx, key)
	}
}

// Bắn thông báo xanh/đỏ cho các User khác
func (m *Manager) notifyStatusChange(userId string, status string) {
	msg, err := json.Marshal(map[string]string{
		"type":   "USER_STATUS",
		"userId": userId,
		"status": status,
	})

	if err != nil {
		return
	}

	// Quăng thông báo cho toàn bộ máy khách (có thể tối ưu lại chỉ gửi cho bạn bè sau)
	for _, client := range m.Clients {
		if client.ID != userId {
			select {
			case client.SendChan <- msg:
			default:
				// Bỏ qua nếu Client này đang bị nghẽn ống
			}
		}
	}
}
