namespace ChatService.Application.Common.Models;

public class ApiResponse<T>
{
    public bool Success { get; set; }
    public string Message { get; set; }
    public T Data { get; set; }
    public ErrorDetail Error { get; set; }

    // Helper tạo response thành công nhanh
    public static ApiResponse<T> Ok(T data, string message = "Thành công")
    {
        return new ApiResponse<T>
        {
            Success = true,
            Message = message,
            Data = data,
            Error = null
        };
    }

    // Helper tạo response lỗi
    public static ApiResponse<T> Fail(string message, ErrorDetail error)
    {
        return new ApiResponse<T>
        {
            Success = false,
            Message = message,
            Data = default,
            Error = error
        };
    }
}

public class ErrorDetail
{
    public string Code { get; set; }
    public string Path { get; set; }
    public DateTime Timestamp { get; set; } = DateTime.UtcNow;
}