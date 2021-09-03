const reducer = (state = null, action) => {
  switch (action.type) {
  case 'SET_NOTIFICATION':
    return { ...action.data }
  case 'CLEAR_NOTIFICATION':
    if (state && action.data.content === state.content)
      return null
    return state
  default:
    return state
  }
}

export default reducer
