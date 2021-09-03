export const login = (user) => {
  return async dispatch => {
    window.localStorage.setItem(
      'loggedUser', JSON.stringify(user)
    )
    dispatch({
      type: 'LOGIN',
      data: user
    })
  }
}

export const initializeUser = () => {
  return async dispatch => {
    const loggedUserJSON = window.localStorage.getItem('loggedUser')
    const user = JSON.parse(loggedUserJSON)
    dispatch({
      type: 'LOGIN',
      data: user
    })
  }
}

export const logout = () => {
  return async dispatch => {
    window.localStorage.removeItem('loggedUser')
    dispatch({
      type: 'LOGOUT'
    })
  }
}