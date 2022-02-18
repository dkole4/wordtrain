import { wordService } from 'services/word-service'
import { logout } from './auth-actions'

export const initializeWords = (user) => {
  return async dispatch => {
    const words = await wordService.getWords(user)

    if (words.status === 200) {
      dispatch({
        type: 'INIT',
        data: words.data
      })
    } else {
      dispatch(logout())
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
      dispatch(logout())
    }
  }
}

export const updateWord = (user, wordPair) => {
  return async dispatch => {
    const updated = await wordService.updateWord(user, wordPair)
    
    if (updated.status === 200) {
      dispatch({
        type: 'UPDATE',
        data: updated.data,
        old: wordPair.id
      })
    } else {
      dispatch(logout())
    }
  }
}

export const updateScore = (user, changes) => {
  return async dispatch => {
    const updated = await wordService.updateScore(user, changes)
    
    if (updated.status === 200) {
      dispatch({
        type: 'UPDATE_SCORE',
        data: updated.data
      })
    } else {
      dispatch(logout())
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
      dispatch(logout())
    }
  }
}