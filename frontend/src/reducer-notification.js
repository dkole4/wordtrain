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

export const setErrorMessage = (content) => {
  return async dispatch => {
    dispatch({
      type: 'SET_NOTIFICATION',
      data: { content, type: 'error' }
    })
    setTimeout(() => {
      dispatch({
        type: 'CLEAR_NOTIFICATION',
        data: { content }
      })
    }, 5000)
  }
}

export const setNotificationMessage = (content) => {
  return async dispatch => {
    dispatch({
      type: 'SET_NOTIFICATION',
      data: { content, type: 'notification' }
    })
    setTimeout(() => {
      dispatch({
        type: 'CLEAR_NOTIFICATION',
        data: { content }
      })
    }, 5000)
  }
}

export default reducer
