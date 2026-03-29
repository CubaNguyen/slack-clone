import { Module } from '@nestjs/common';
import { OutboxEventsService } from './outbox-events.service';
import { OutboxEventsController } from './outbox-events.controller';

@Module({
  controllers: [OutboxEventsController],
  providers: [OutboxEventsService],
})
export class OutboxEventsModule {}
