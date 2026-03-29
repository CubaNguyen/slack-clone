using MediatR;
using Application.Common.Interfaces; // 👈 Inject Interface thay vì class thực thi
using ChatService.Domain.Enums;
using ChatService.Domain.Entities; // 👈 Thêm cái này để lấy ChannelReplica
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Common.Interfaces;

namespace ChatService.Application.Channels.Commands.SyncChannel
{
    public class SyncChannelHandler : IRequestHandler<SyncChannelCommand>
    {
        private readonly IApplicationDbContext _context; // 👈 Dùng Interface cho đúng chuẩn Clean

        public SyncChannelHandler(IApplicationDbContext context)
        {
            _context = context;
        }

        public async Task Handle(SyncChannelCommand request, CancellationToken cancellationToken)
        {
            // 1. Tìm xem channel đã tồn tại trong bảng Replica chưa
            var existingChannel = await _context.ChannelReplicas
                .FirstOrDefaultAsync(x => x.Id == request.Id, cancellationToken);

            if (existingChannel == null)
            {
                // --- LOGIC INSERT ---
                var newReplica = new ChannelReplica
                {
                    Id = request.Id,
                    WorkspaceId = request.WorkspaceId,
                    Name = request.Name,

                    // BỎ (int) ĐI LÀ XONG 👇
                    Type = Enum.Parse<ChannelType>(request.Type, true),

                    ArchivedAt = request.IsArchived ? DateTime.UtcNow : null
                };

                _context.ChannelReplicas.Add(newReplica);
            }
            else
            {
                // --- LOGIC UPDATE ---
                existingChannel.Name = request.Name;

                // TƯƠNG TỰ Ở ĐÂY 👇
                existingChannel.Type = Enum.Parse<ChannelType>(request.Type, true);

                existingChannel.ArchivedAt = request.IsArchived ? DateTime.UtcNow : null;
            }

            // 2. Lưu xuống DB
            await _context.SaveChangesAsync(cancellationToken);

        }
    }
}