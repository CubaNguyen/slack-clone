package websocket

import (
	"github.com/redis/go-redis/v9"
)

// Cấu trúc để gửi lệnh vào/ra phòng
type RoomAction struct {
	Client *Client
	RoomID string
}

type Manager struct {
	Clients     map[string]*Client
	Rooms       map[string]map[string]*Client
	Broadcast   chan []byte
	Register    chan *Client
	Unregister  chan *Client
	JoinRoom    chan *RoomAction
	LeaveRoom   chan *RoomAction
	RedisClient *redis.Client
}

func NewManager(rdb *redis.Client) *Manager {
	return &Manager{
		Clients:     make(map[string]*Client),
		Rooms:       make(map[string]map[string]*Client),
		Broadcast:   make(chan []byte),
		Register:    make(chan *Client),
		Unregister:  make(chan *Client),
		JoinRoom:    make(chan *RoomAction),
		LeaveRoom:   make(chan *RoomAction),
		RedisClient: rdb,
	}
}

// Hàm Run giờ đóng vai trò như "Trạm phân luồng giao thông"
func (m *Manager) Run() {
	for {
		select {
		case client := <-m.Register:
			m.handleRegister(client)

		case client := <-m.Unregister:
			m.handleUnregister(client)

		case action := <-m.JoinRoom:
			m.handleJoinRoom(action)

		case action := <-m.LeaveRoom:
			m.handleLeaveRoom(action)

		case rawMessage := <-m.Broadcast:
			m.handleBroadcast(rawMessage)
		}
	}
}
