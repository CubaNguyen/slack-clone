using MediatR;
using Microsoft.AspNetCore.Mvc;
using ChatService.Application.Common.Models;
using ChatService.Application.Messages.Commands.SendMessage;
using ChatService.Application.Messages.Commands.ToggleReaction;
using ChatService.Application.Messages.Commands.DeleteMessage;

using ChatService.DTOs; // ApiResponse của bạn
using ChatService.Application.Messages.Queries.GetMessages;
using ChatService.Presentation.Controllers.Dtos;
using ChatService.Application.Messages.Queries.GetThreadMessages;
using ChatService.Application.Messages.Commands.TogglePin;
using Application.Messages.Commands.EditMessage;
[ApiController]
[Route("api/[controller]")]
public class MessagesController : ControllerBase
{
    private readonly IMediator _mediator;


    public MessagesController(IMediator mediator)
    {
        _mediator = mediator;
    }

    /// <summary>
    /// Gửi tin nhắn mới vào một kênh chat.
    /// </summary>
    /// <param name="request">Thông tin tin nhắn bao gồm ChannelId và Nội dung</param>
    /// <returns>Trả về ID của tin nhắn vừa tạo</returns>
    /// <response code="200">Gửi thành công</response>
    /// <response code="400">Dữ liệu không hợp lệ</response>
    [HttpPost]
    public async Task<IActionResult> SendMessage([FromBody] SendMessageRequest request)
    {
        // ✅ TRUYỀN ĐỦ THAM SỐ VÀO ĐÂY:
        var command = new SendMessageCommand(
            request.ChannelId,
            request.Content,
           request.Type,      // Đưa Type lên trước (Vị trí số 3)
    request.ParentId     // 👈 Và cả thằng này nữa nếu cần
        );

        var messageId = await _mediator.Send(command);

        return Ok(ApiResponse<Guid>.Ok(messageId, "Gửi tin nhắn thành công!"));
    }

    /// <summary>
    /// API Lấy lịch sử tin nhắn của một Channel (Phân trang theo kiểu Cursor)
    /// </summary>
    /// <param name="channelId">ID của kênh chat</param>
    /// <param name="before">Mốc thời gian để lấy các tin nhắn cũ hơn (Cursor)</param>
    /// <param name="limit">Số lượng tin nhắn tối đa mỗi lần lấy</param>
    [HttpGet("{channelId}")]
    public async Task<ActionResult<ApiResponse<List<GetMessageResponse>>>> GetMessages(
      [FromRoute] Guid channelId,
      [FromQuery] GetMessagesRequest request) // Dùng Request DTO riêng
    {
        // Mapping thủ công từ Request sang Query
        var query = new GetMessagesQuery
        {
            ChannelId = channelId,
            Before = request.Before?.ToUniversalTime(),
            Limit = Math.Min(request.Limit, 100)
        };

        var result = await _mediator.Send(query);

        // BẠN PHẢI GÁN GIÁ TRỊ NHƯ THẾ NÀY:
        var response = result.Select(m => new GetMessageResponse
        {
            Id = m.Id,
            Content = m.Content,
            CreatedAt = m.CreatedAt,
            Type = m.Type,
            UserId = m.UserId,
            ParentId = m.ParentId,
            ReplyCount = m.ReplyCount,
            LatestReplyAt = m.LatestReplyAt,
            Reactions = m.Reactions

        }).ToList();

        return Ok(ApiResponse<List<GetMessageResponse>>.Ok(response));
    }
    /// <summary>
    /// API Lấy chi tiết một Thread (Luồng thảo luận)
    /// </summary>
    /// <param name="channelId">ID của kênh chứa Thread</param>
    /// <param name="parentId">ID của tin nhắn gốc (Parent Message)</param>
    /// <returns>Trả về tin nhắn gốc, danh sách reply và thống kê metadata</returns>
    [HttpGet("{channelId}/threads/{parentId}")]
    public async Task<ActionResult<ApiResponse<ThreadDetailDto>>> GetThreadDetail(
    [FromRoute] Guid channelId,
    [FromRoute] Guid parentId)
    {
        var query = new GetThreadMessagesQuery(channelId, parentId);
        var result = await _mediator.Send(query);
        if (result == null) return NotFound("Thread này không tồn tại!");
        return Ok(ApiResponse<ThreadDetailDto>.Ok(result));
    }


    /// <summary>
    /// Thêm hoặc rút lại cảm xúc (Reaction) cho một tin nhắn
    /// </summary>
    [HttpPost("{channelId}/messages/{messageId}/reactions")]
    public async Task<ActionResult<ApiResponse<bool>>> ToggleReaction(
        [FromRoute] Guid channelId,
        [FromRoute] Guid messageId,
        [FromBody] string emoji) // Có thể tạo DTO cho body nếu thích, ví dụ: { "emoji": ":heart:" }
    {
        var command = new ToggleReactionCommand(channelId, messageId, emoji);
        var isAdded = await _mediator.Send(command);

        return Ok(ApiResponse<bool>.Ok(isAdded, isAdded ? "Đã thả cảm xúc" : "Đã rút lại cảm xúc"));
    }

    /// <summary>
    /// Ghim hoặc bỏ ghim một tin nhắn trong kênh.
    /// </summary>
    [HttpPost("{channelId}/messages/{messageId}/pin")]
    public async Task<ActionResult<ApiResponse<bool>>> TogglePin(Guid channelId, Guid messageId)
    {
        return Ok(ApiResponse<bool>.Ok(await _mediator.Send(new TogglePinCommand(channelId, messageId))));
    }
    /// <summary>
    /// Lấy danh sách các tin nhắn đã ghim trong kênh.
    /// </summary>
    [HttpGet("{channelId}/pins")]
    public async Task<ActionResult<ApiResponse<List<MessageDto>>>> GetPins(Guid channelId)
    {
        return Ok(ApiResponse<List<MessageDto>>.Ok(await _mediator.Send(new GetPinnedMessagesQuery(channelId))));
    }

    /// <summary>
    /// Xóa soft một tin nhắn trong kênh.
    /// </summary>
    [HttpDelete("{channelId}/messages/{messageId}")]
    public async Task<ActionResult<ApiResponse<bool>>> DeleteMessage(Guid channelId, Guid messageId)
    {
        return Ok(ApiResponse<bool>.Ok(await _mediator.Send(new DeleteMessageCommand(channelId, messageId))));
    }

    /// <summary>
    /// Sửa nội dung một tin nhắn (Chỉ người viết mới được sửa)
    /// </summary>
    [HttpPut("{channelId}/messages/{messageId}")]
    public async Task<ActionResult<ApiResponse<bool>>> Edit(Guid channelId, Guid messageId, [FromBody] EditMessageRequest request)
    {
        return Ok(ApiResponse<bool>.Ok(await _mediator.Send(new EditMessageCommand(channelId, messageId, request.Content))));
    }

    public record EditMessageRequest(string Content);
}

