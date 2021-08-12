import React from 'react'
import { useSelector } from 'react-redux'
import { Container, Message } from 'semantic-ui-react'

export const Notification = () => {
  const message = useSelector(state => state.notification)

  if (!message) {
    return (
      <Container></Container>
    )
  }

  return (
    <Container>
      <Message warning={message.type === 'error'}>
        <Message.Header>
          {message.content}
        </Message.Header>
      </Message>
    </Container>
  )
}