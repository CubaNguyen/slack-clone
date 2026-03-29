import { Controller } from '@nestjs/common';
import { GrpcMethod } from '@nestjs/microservices';
import { UsersService } from './users.service'; // Service chứa logic DB

@Controller()
export class UserGrpcController {
  constructor(private readonly userService: UsersService) {}

  @GrpcMethod('UserService', 'GetUserForInvite')
  async getUserForInvite(data: { email: string }) {
    // Gọi hàm bạn vừa viết
    const user = await this.userService.findByEmail(data.email);

    // Nếu null -> Trả về rỗng (Java bên kia sẽ hiểu là không tìm thấy)
    if (!user) {
      return {};
    }

    // Map dữ liệu từ User Entity + Profile sang gRPC Response
    return {
      id: user.id, // ID dạng UUID string
      email: user.email,
    };
  }
}
