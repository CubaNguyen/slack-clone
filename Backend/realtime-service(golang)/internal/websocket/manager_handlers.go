package websocket

import (
	"encoding/json"
	"log"
)

func (m *Manager) handleRegister(client *Client) {
	m.Clients[client.ID] = client
	m.updatePresence(client.ID, true)
	m.notifyStatusChange(client.ID, "online")
	log.Printf("👤 User %s online.", client.ID)
}

func (m *Manager) handleUnregister(client *Client) {
	if _, ok := m.Clients[client.ID]; ok {
		m.updatePresence(client.ID, false)
		m.notifyStatusChange(client.ID, "offline")

		// Dọn dẹp: Rời khỏi tất cả các phòng trước khi xóa hoàn toàn
		for roomID := range client.Rooms {
			delete(m.Rooms[roomID], client.ID)
		}

		delete(m.Clients, client.ID)
		close(client.SendChan)
		log.Printf("❌ User %s offline.", client.ID)
	}
}

func (m *Manager) handleJoinRoom(action *RoomAction) {
	if m.Rooms[action.RoomID] == nil {
		m.Rooms[action.RoomID] = make(map[string]*Client)
	}
	m.Rooms[action.RoomID][action.Client.ID] = action.Client
	action.Client.Rooms[action.RoomID] = true
	log.Printf("🏠 User %s đã tham gia phòng %s", action.Client.ID, action.RoomID)
}

func (m *Manager) handleLeaveRoom(action *RoomAction) {
	if clients, ok := m.Rooms[action.RoomID]; ok {
		delete(clients, action.Client.ID)
		delete(action.Client.Rooms, action.RoomID)
		log.Printf("🚪 User %s đã rời phòng %s", action.Client.ID, action.RoomID)

		// Trả lại RAM: Nếu phòng trống rỗng thì xóa luôn cái phòng đó
		if len(clients) == 0 {
			delete(m.Rooms, action.RoomID)
		}
	}
}

func (m *Manager) handleBroadcast(rawMessage []byte) {
	var data struct {
		ChannelId string `json:"channelId"`
		UserId    string `json:"userId"`
	}
	if err := json.Unmarshal(rawMessage, &data); err != nil {
		log.Printf("❌ Lỗi giải mã tin nhắn từ Redis: %v", err)
		return
	}

	clientsInRoom, ok := m.Rooms[data.ChannelId]
	if !ok {
		return // Phòng không có ai đang mở tab thì bỏ qua
	}

	count := 0
	for _, client := range clientsInRoom {
		// Không gửi lại cho chính người nhắn (Tránh Echo UI)
		if client.ID == data.UserId {
			continue
		}

		select {
		case client.SendChan <- rawMessage:
			count++
		default:
			log.Printf("⚠️ Client %s lag quá, hệ thống tự động ngắt kết nối!", client.ID)
			// Chạy ngầm Goroutine để không làm kẹt hàm Run()
			go func(c *Client) {
				m.Unregister <- c
			}(client)
		}
	}

	if count > 0 {
		log.Printf("🚀 Đã chuyển tin từ %s tới %d người trong phòng %s", data.UserId, count, data.ChannelId)
	}
}
