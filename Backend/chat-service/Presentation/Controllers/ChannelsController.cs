namespace ChatService.Presentation.Controllers;

using ChatService.Application.Channels.Commands.MarkAsRead;
using ChatService.Application.Common.Models;
using MediatR;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/[controller]")]
public class ChannelsController : ControllerBase
{
    private readonly IMediator _mediator;
    public ChannelsController(IMediator mediator) => _mediator = mediator;


    /// <summary>
    /// Đánh dấu đã xem toàn bộ tin nhắn trong một kênh.
    /// </summary>
    /// <param name="id">ID của Channel</param>
    /// <response code="200">Thao tác thành công</response>
    [HttpPost("{id}/read")]
    public async Task<IActionResult> MarkAsRead(Guid id)
    {
        await _mediator.Send(new MarkAsReadCommand(id));
        return Ok(ApiResponse<object>.Ok(null, "Đã đánh dấu xem"));
    }
}