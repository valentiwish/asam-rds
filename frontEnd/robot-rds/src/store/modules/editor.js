import { EditorActiveType } from '@/components/meta2d/constant'

const state = {
  editorScale: 100,
  oftenIcons: [],
  editorActiveType: EditorActiveType.CanvasActive,
  currentPen: {},
  currentPenRect: {}
}

const mutations = {
  setEditorScale(state, n) {
    return state.editorScale = n;
  },
  setEditorActiveType(state, n) {
    return state.editorActiveType = n
  },
  setCurrentPen(state, n) {
    return state.currentPen = n
  },
  setCurrentPenRect(state, n) {
    return state.currentPenRect = n
  },
  addOftenIcon(icon) {
    const iconArr = this.oftenIcons
    const index = iconArr.findIndex((item) => {
      return item.name === icon.name
    })
    if (index > -1) {
      iconArr.splice(index, 1)
    }
    const newLength = iconArr.unshift(icon)
    if (newLength > 8) {
      iconArr.splice(8, 1)
    }
  }
}

const actions = {
  addOftenIcon(icon) {
    const iconArr = this.oftenIcons
    const index = iconArr.findIndex((item) => {
      return item.name === icon.name
    })
    if (index > -1) {
      iconArr.splice(index, 1)
    }
    const newLength = iconArr.unshift(icon)
    if (newLength > 8) {
      iconArr.splice(8, 1)
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
