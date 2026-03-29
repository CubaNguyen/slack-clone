import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './modules/users/users.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './modules/users/entities/user.entity';
import { Profile } from './modules/profile/entities/profile.entity';
import { Session } from './modules/session/entities/session.entity';
import { VerificationCode } from './modules/verification-code/entities/verification-code.entity';
import { ProfileModule } from './modules/profile/profile.module';
import { SessionModule } from './modules/session/session.module';
import { VerificationCodeModule } from './modules/verification-code/verification-code.module';
import { APP_GUARD } from '@nestjs/core';
import { AuthGuard } from '@nestjs/passport';
import { JwtAuthGuard } from './auth/passport/jwt-auth.guard';
import { MailService } from './modules/mailer/mail.service';
import { OutboxEventsModule } from './modules/outbox-events/outbox-events.module';
import { OutboxEvent } from './modules/outbox-events/entities/outbox-events.entity';

@Module({
  imports: [
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule], // đây là module bạn muốn import trước
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        type: 'postgres',
        host: configService.get<string>('DB_HOST'),
        port: configService.get<number>('DB_PORT'),
        username: configService.get<string>('DB_USERNAME'),
        password: configService.get<string>('DB_PASSWORD'),
        database: configService.get<string>('DB_NAME'),
        entities: [User, Profile, Session, VerificationCode, OutboxEvent],
        synchronize: true,
      }),
    }),

    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '.env',
    }),
    AuthModule,
    UsersModule,
    ProfileModule,
    SessionModule,
    VerificationCodeModule,
    OutboxEventsModule,
  ],
  controllers: [AppController],
  providers: [
    AppService,
    MailService,
    { provide: APP_GUARD, useClass: JwtAuthGuard },
  ],
})
export class AppModule {}
