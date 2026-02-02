const state = {
  minHeader:true,
}

const mutations = {
  toggleThemeMinHeader(state, n){
    if(void 0 == n){
        return state.minHeader = !state.minHeader;
    }
    else{
        return state.minHeader = n;
    } 
  }
}
const actions = {
  
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
