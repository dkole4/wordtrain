import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'


(async () => {
  const { App } = await import('./app')
  const { store } = await import('./store/store')

  ReactDOM.render(
    <Provider store={store}>
      <App />
    </Provider>,
    document.getElementById('root')
  )

})()

