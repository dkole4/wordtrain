import React from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container, Message } from 'semantic-ui-react'

import { clearMessage } from 'store/actions/notification-actions'

const messageStyle = {
  position: 'absolute',
  zIndex: 10,
  bottom: '15px',
}

export const Notification = () => {
  const message = useSelector(state => state.notification)
  const dispatch = useDispatch()

  const handleDismiss = () =>
    dispatch(clearMessage(message.content))

  return (
    <Container style={messageStyle} >
      { message !== null && 
        <Message
          floating
          onDismiss={handleDismiss}
          warning={message.type === 'error'}
          header={message.content}
        />
      }
    </Container>
  )
}