const reducer = (state = [], action) => {
  switch (action.type) {
  case 'UPDATE':
    return state.map(a => a.id !== action.old ? a : { ...a, ...action.data })
  case 'UPDATE_SCORE':
    return state.map(a => {
      const ids = action.data.map(b => b.word_id)
      if (ids.includes(a.id)) {
        const word = action.data.find(b => b.word_id = a.id)
        return {
          ...a,
          score: word.score, 
          tries: word.tries
        }
      } else {
        return a
      }
    })
  case 'NEW':
    return [ ...state, ...action.data ]
  case 'DELETE':
    return state.filter(word => word.id !== action.data.id)
  case 'INIT':
    return action.data
  default:
    return state
  }
}

export default reducer