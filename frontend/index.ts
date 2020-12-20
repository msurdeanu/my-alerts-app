import {Flow} from '@vaadin/flow-frontend/Flow';
import {Router} from '@vaadin/router';

const {serverSideRoutes} = new Flow({
  imports: () => import('../target/frontend/generated-flow-imports')
});

const routes = [
  ...serverSideRoutes
];

const router = new Router(document.querySelector('#outlet'));
router.setRoutes(routes);
