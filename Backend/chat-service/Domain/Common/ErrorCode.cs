using System.Net;

namespace ChatService.Domain.Common;

public record ErrorCode(string Code, string Message, HttpStatusCode StatusCode)
{
    // === 1. LỖI HỆ THỐNG ===
    public static readonly ErrorCode Uncategorized = new("SYS_999", "Lỗi hệ thống không xác định", HttpStatusCode.InternalServerError);

    // === 2. LỖI AUTH & USER ===
    public static readonly ErrorCode Unauthenticated = new("AUTH_001", "Vui lòng đăng nhập để tiếp tục.", HttpStatusCode.Unauthorized);
    public static readonly ErrorCode UserNotFound = new("USER_001", "Người dùng không tồn tại.", HttpStatusCode.NotFound);

    // === 3. LỖI WORKSPACE ===
    public static readonly ErrorCode WorkspaceNotFound = new("WS_001", "Workspace không tồn tại.", HttpStatusCode.NotFound);
    public static readonly ErrorCode NotInWorkspace = new("WS_006", "Bạn không phải thành viên của Workspace này.", HttpStatusCode.Forbidden);
    // === 4. LỖI CHANNEL (MỚI THÊM) ===
    public static readonly ErrorCode ChannelNotFound = new("CH_001", "Kênh không tồn tại.", HttpStatusCode.NotFound);
    public static readonly ErrorCode NotInChannel = new("CH_002", "Bạn không phải thành viên của Kênh chat này.", HttpStatusCode.Forbidden);
}