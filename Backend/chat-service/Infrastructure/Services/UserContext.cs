using Application.Common.Interfaces;
using Microsoft.AspNetCore.Http;

namespace Infrastructure.Services;

public class UserContext : ICurentUserContext
{
    private readonly IHttpContextAccessor _httpContextAccessor;

    public UserContext(IHttpContextAccessor httpContextAccessor) => _httpContextAccessor = httpContextAccessor;

    // public string? UserId => _httpContextAccessor.HttpContext?.Request.Headers["X-User-Id"];

    public Guid UserId
    {
        get
        {
            // Đọc từ Header do Gateway truyền xuống
            var sid = _httpContextAccessor.HttpContext?.Request.Headers["X-User-Id"].ToString();

            if (Guid.TryParse(sid, out Guid guid))
            {
                return guid;
            }

            // Trả về một Guid mock mặc định khi dev (nhớ thay bằng ID thật trong DB của bạn để test)
            return Guid.Parse("a42fdce5-e17c-4dea-a079-999079c68452");
        }
    }
}