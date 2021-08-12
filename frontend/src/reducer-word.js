import { wordService } from './services'

const reducer = (state = [], action) => {
  switch (action.type) {
  case 'UPDATE':
    return state.map(a => a.id !== action.old ? a : { ...a, ...action.data })
  case 'UPDATE_SCORE':
    return state.map(a => {
      const ids = action.data.map(b => b.word_id)
      console.log(ids)
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

export const initializeWords = (user) => {
  return async dispatch => {
    const words = await wordService.getWords(user)

    if (words.status === 200) {
      dispatch({
        type: 'INIT',
        data: words.data
      })
    } else {
      dispatch({
        type: 'LOGOUT'
      })
    }
  }
}

export const createWords = (user, newWords) => {
  return async dispatch => {
    const created = await wordService.insertWord(user, newWords)
    
    if (created.status === 201) {
      dispatch({
        type: 'NEW',
        data: created.data
      })
    } else {
      dispatch({
        type: 'LOGOUT'
      })
    }
  }
}

export const updateWord = (id, user, wordPair) => {
  return async dispatch => {
    const updated = await wordService.updateWord(id, user, wordPair)
    
    if (updated.status === 201) {
      dispatch({
        type: 'UPDATE',
        data: updated.data,
        old: id
      })
    } else {
      dispatch({
        type: 'LOGOUT'
      })
    }
  }
}

export const updateScore = (user, changes) => {
  return async dispatch => {
    const updated = await wordService.updateScore(user, changes)
    
    if (updated.status === 201) {
      dispatch({
        type: 'UPDATE_SCORE',
        data: updated.data
      })
    } else {
      dispatch({
        type: 'LOGOUT'
      })
    }
  }
}

export const deleteWord = (user, wordId) => {
  return async dispatch => {
    const response = await wordService.deleteWord(user, wordId)
    
    if (response.status === 204) {
      dispatch({
        type: 'DELETE',
        data: { id: wordId }
      })
    } else {
      dispatch({
        type: 'LOGOUT'
      })
    }
  }
}

export default reducer