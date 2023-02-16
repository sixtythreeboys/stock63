import { Eureka } from 'eureka-js-client';
import config from 'config';

const client = new Eureka(config.EUREKA);

export function apply() {
  client.start();
  process.on('SIGINT', () => {
    client.stop(() => {
      console.log('Node.js app unregistered from Eureka server');
      process.exit();
    });
  });
}
