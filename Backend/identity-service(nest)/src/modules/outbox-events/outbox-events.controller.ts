import { Controller } from '@nestjs/common';
import { OutboxEventsService } from './outbox-events.service';

@Controller('outbox-events')
export class OutboxEventsController {
  constructor(private readonly outboxEventsService: OutboxEventsService) {}
}
