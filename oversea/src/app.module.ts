import { Module } from '@nestjs/common';
import { OverseaModule } from './oversea/oversea.module';

@Module({
  imports: [OverseaModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
