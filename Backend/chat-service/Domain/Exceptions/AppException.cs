using ChatService.Domain.Common;

namespace ChatService.Domain.Exceptions;

public class AppException : Exception
{
    public ErrorCode ErrorCode { get; }

    public AppException(ErrorCode errorCode) : base(errorCode.Message)
    {
        ErrorCode = errorCode;
    }
}