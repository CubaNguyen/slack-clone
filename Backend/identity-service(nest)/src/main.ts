import { ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { NestFactory } from '@nestjs/core';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger'; // <--- 1. Import Swagger
import { join } from 'path/win32';
import { AppModule } from './app.module';
import { AllExceptionsFilter } from './interceptors/http-exception.filter';
import { ResponseInterceptor } from './interceptors/response.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const configService = app.get(ConfigService);
  const port = configService.get<number>('PORT') ?? 3001;
  // 2. Kết nối Microservice gRPC
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: 'com.project.user.grpc', // ⚠️ Phải trùng với "package" trong file .proto
      protoPath: join(__dirname, 'proto/user.proto'), // Đường dẫn tới file .proto
      url: '0.0.0.0:9090', // ⚠️ Port 9090 phải trùng với port Java Client gọi tới
    },
  });
  // Thiết lập prefix global cho API (ví dụ: localhost:3001/api/v1/users)
  app.setGlobalPrefix('/api/v1');

  app.useGlobalInterceptors(new ResponseInterceptor());
  app.useGlobalFilters(new AllExceptionsFilter());

  // Config validation
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      stopAtFirstError: true,
    }),
  );

  // --- CẤU HÌNH SWAGGER (BẮT ĐẦU) ---
  const config = new DocumentBuilder()
    .setTitle('Slack Clone API') // Tiêu đề tài liệu
    .setDescription('Deep Backend API Documentation') // Mô tả
    .setVersion('1.0')
    .addBearerAuth() // <--- Quan trọng: Thêm nút nhập Token JWT
    .build();

  const document = SwaggerModule.createDocument(app, config);

  // Setup đường dẫn truy cập Swagger
  // Đường dẫn sẽ là: http://localhost:3001/docs
  SwaggerModule.setup('docs', app, document);
  // --- CẤU HÌNH SWAGGER (KẾT THÚC) ---
  await app.startAllMicroservices();
  await app.listen(port);

  console.log(`Application is running on: http://localhost:${port}/api/v1`);
  console.log(`Swagger Docs is running on: http://localhost:${port}/docs`);
  console.log('Identity Service is running gRPC on port 9090');
}
bootstrap();
