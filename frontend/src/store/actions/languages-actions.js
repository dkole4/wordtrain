

export const initializeLanguages = (languages) => {
  return async dispatch => {

    dispatch({
      type: 'SET_LANGUAGES',
      data: languages
    })
  }
}