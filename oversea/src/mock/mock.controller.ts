import { Controller, Get } from '@nestjs/common';
import { MockService } from './mock.service';

@Controller('mock')
export class MockController {
  constructor(private readonly mock: MockService) {}
  @Get('test')
  test() {}
}
