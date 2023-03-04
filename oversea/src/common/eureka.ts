import { Eureka } from 'eureka-js-client';
import config from 'config';

export function apply() {
  const client = new Eureka(config.EUREKA);
  client.start();
  process.on('SIGINT', () => {
    client.stop(() => {
      console.log('Node.js app unregistered from Eureka server');
      process.exit();
    });
  });
}
