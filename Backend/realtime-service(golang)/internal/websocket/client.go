package websocket

import (
	"encoding/json"
	"log"
	"time"

	"github.com/gorilla/websocket"
)

const (
	// Thời gian chờ tối đa để nhận gói Pong từ Client.
	pongWait = 25 * time.Second
	// Khoảng cách giữa mỗi lần Server chủ động gửi gói Ping xuống Client.
	pingPeriod = 20 * time.Second
	// Thời gian tối đa để nhồi một tin nhắn (hoặc Ping) vào mạng.
	writeWait = 10 * time.Second
	// Kích thước tối đa của một tin nhắn (tính bằng bytes).
	maxMessageSize = 4096
)

// Cấu trúc chuẩn để hứng mọi loại lệnh (Command) từ Frontend gửi lên
// Thêm omitempty để bỏ qua những trường FE không gửi
type ClientCommand struct {
	Action    string `json:"action"`
	RoomID    string `json:"room,omitempty"`
	IsTyping  bool   `json:"isTyping,omitempty"`  // Tương lai dùng cho: Đang gõ phím
	MessageID string `json:"messageId,omitempty"` // Tương lai dùng cho: Đã xem tin nhắn
	Status    string `json:"status,omitempty"`    // Tương lai dùng cho: Đổi trạng thái DND/Away
}

// Client đại diện cho một kết nối WebSocket từ người dùng
type Client struct {
	ID       string
	Rooms    map[string]bool
	Conn     *websocket.Conn
	Manager  *Manager
	SendChan chan []byte
}

func NewClient(id string, channelId string, conn *websocket.Conn, manager *Manager) *Client {
	return &Client{
		ID:       id,
		Rooms:    make(map[string]bool),
		Conn:     conn,
		Manager:  manager,
		SendChan: make(chan []byte, 256),
	}
}

// --- READ PUMP (MÁY BƠM HÚT) ---
func (c *Client) ReadPump() {
	defer func() {
		c.Manager.Unregister <- c
		c.Conn.Close()
	}()

	c.Conn.SetReadLimit(maxMessageSize)
	c.Conn.SetReadDeadline(time.Now().Add(pongWait))
	c.Conn.SetPongHandler(func(string) error {
		c.Conn.SetReadDeadline(time.Now().Add(pongWait))
		return nil
	})

	for {
		_, message, err := c.Conn.ReadMessage()
		if err != nil {
			// Bắt các lỗi rớt mạng hoặc đóng trình duyệt để log ra (nếu cần debug)
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				log.Printf("⚠️ Lỗi ngắt kết nối đột ngột: %v", err)
			}
			break
		}

		// Giải mã tin nhắn vào cấu trúc ClientCommand
		var command ClientCommand
		if err := json.Unmarshal(message, &command); err != nil {
			log.Printf("❌ Nhận chuỗi JSON rác từ Client %s: %v", c.ID, string(message))
			continue // Kệ nó, qua xử lý tin nhắn tiếp theo
		}

		// BỘ ĐỊNH TUYẾN LỆNH (COMMAND ROUTER) - Rất dễ mở rộng
		switch command.Action {

		case "join":
			if command.RoomID != "" {
				c.Manager.JoinRoom <- &RoomAction{Client: c, RoomID: command.RoomID}
			} else {
				log.Printf("⚠️ Lỗi logic: Client %s gửi action 'join' nhưng thiếu 'room'", c.ID)
			}

		case "leave":
			if command.RoomID != "" {
				c.Manager.LeaveRoom <- &RoomAction{Client: c, RoomID: command.RoomID}
			}

		// --- KHU VỰC CHUẨN BỊ CHO CÁC TÍNH NĂNG TƯƠNG LAI ---
		case "typing":
			// Mẫu logic: Đóng gói và báo cho Manager phát sóng trạng thái typing
			// c.Manager.TypingChan <- &TypingAction{RoomID: command.RoomID, UserID: c.ID, IsTyping: command.IsTyping}
			log.Printf("Báo cáo: User %s đang gõ: %v trong phòng %s", c.ID, command.IsTyping, command.RoomID)

		case "mark_read":
			// Mẫu logic: Gọi API C# hoặc báo thẳng qua Manager
			log.Printf("User %s đã xem tin nhắn %s", c.ID, command.MessageID)

		case "change_status":
			// Mẫu logic: Cập nhật biến Redis
			log.Printf("User %s đổi trạng thái thành %s", c.ID, command.Status)

		default:
			log.Printf("❓ Client gửi action không hợp lệ: %s", command.Action)
		}
	}
}

// --- WRITE PUMP (MÁY BƠM XẢ) ---
func (c *Client) WritePump() {
	ticker := time.NewTicker(pingPeriod)
	defer func() {
		ticker.Stop()
		c.Conn.Close()
	}()

	for {
		select {
		case message, ok := <-c.SendChan:
			c.Conn.SetWriteDeadline(time.Now().Add(writeWait))

			if !ok {
				c.Conn.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}

			if err := c.Conn.WriteMessage(websocket.TextMessage, message); err != nil {
				return
			}

		case <-ticker.C:
			c.Conn.SetWriteDeadline(time.Now().Add(writeWait))
			if err := c.Conn.WriteMessage(websocket.PingMessage, nil); err != nil {
				return
			}
		}
	}
}
