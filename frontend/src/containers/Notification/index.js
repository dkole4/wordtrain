import React from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Container, Message } from 'semantic-ui-react'

import { clearMessage } from 'store/actions/notification-actions'

export const Notification = () => {
  const message = useSelector(state => state.notification)
  const dispatch = useDispatch()

  const handleDismiss = () =>
    dispatch(clearMessage(message.content))

  if (!message) return <Container />

  return (
    <Container>
      <Message
        floating
        onDismiss={handleDismiss}
        warning={message.type === 'error'}
        header={message.content}
      />
    </Container>
  )
}