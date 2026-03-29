import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { CustomJwtPayload } from 'src/customInterface/JwtPayload';

export type JwtPayload = {
  sub: string | number;
  username: string;
  role?: string;
};

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(private config: ConfigService) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: config.get<string>('JWT_SECRET')!,
    });
  }

  // Nếu token hợp lệ, giá trị trả về sẽ gán vào req.user
  async validate(payload: CustomJwtPayload) {
    return {
      id: payload.sub,
      email: payload.email,
    };
  }
}
