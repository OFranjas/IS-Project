# TO-DOs

- [ ] Introduce  delays  on  the  server  side  to  emulate  network  delays.
- [ ] One client-side querie able to tolerate network failures.
  - Retries  up  to  three  times  to  reconnect,  before  giving  up.
  - May  create  a 
special  service  on  the  server  and  a  special  query  on  the  client,  outside  any  time 
control, to emulate this case 
- [ ] Optimize clien-side operations