import { createStore, applyMiddleware, combineReducers } from 'redux'
import thunk from 'redux-thunk'

import wordReducer from './reducers/word-reducer'
import authReducer from './reducers/auth-reducer'
import notificationReducer from './reducers/notification-reducer'
// import languageReducer from './store/reducers/languages-reducer'

const reducer = combineReducers({
  words: wordReducer,
  loggedUser: authReducer,
  notification: notificationReducer,
  // languages: languageReducer
})

export const store = createStore(reducer, applyMiddleware(thunk))

export default store
