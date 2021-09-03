const reducer = (state = [], action) => {
  switch (action.type) {
  case 'SET_LANGUAGES':
    return action.data
  default:
    return state
  }
}

export default reducer