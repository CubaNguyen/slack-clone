import {
  ArgumentsHost,
  Catch,
  ExceptionFilter,
  HttpException,
  HttpStatus,
} from '@nestjs/common';

@Catch()
export class AllExceptionsFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse();
    const request = ctx.getRequest();

    let status = HttpStatus.INTERNAL_SERVER_ERROR;
    let message = 'Internal server error';
    let errorCode = 'INTERNAL_ERROR';

    if (exception instanceof HttpException) {
      status = exception.getStatus();
      const res = exception.getResponse();

      // Kiểm tra nếu res là object thì lấy message và code, nếu là string thì lấy chính nó
      if (typeof res === 'object' && res !== null) {
        message = (res as any).message || exception.message;
        errorCode = (res as any).code || 'HTTP_EXCEPTION';
      } else {
        message = res as string;
      }
    }

    response.status(status).json({
      success: false,
      message,
      data: null,
      error: {
        code: errorCode,
        path: request.url,
        timestamp: new Date().toISOString(),
      },
    });
  }
}
