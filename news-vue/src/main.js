import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueAxios from "vue-axios";
import axios from 'axios'

Vue.config.productionTip = false;

Vue.use(VueAxios,axios);
Vue.use(ElementUI);

new Vue({
  render: h => h(App),
}).$mount('#app');
