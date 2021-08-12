import { createStore, applyMiddleware, combineReducers } from 'redux'
import thunk from 'redux-thunk'

import wordReducer from './reducer-word'
import userReducer from './reducer-user'
import notificationReducer from './reducer-notification'

const reducer = combineReducers({
  words: wordReducer,
  loggedUser: userReducer,
  notification: notificationReducer
})

export const store = createStore(reducer, applyMiddleware(thunk))

export default store
