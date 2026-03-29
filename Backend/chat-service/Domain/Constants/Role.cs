namespace ChatService.Domain.Constants
{
    public static class Roles
    {
        public const string Owner = "OWNER";
        public const string Admin = "ADMIN";
        public const string User = "USER";
        public const string System = "SYSTEM"; // Dành cho các event do hệ thống tự chạy
    }
}