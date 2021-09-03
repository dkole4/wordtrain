import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { 
  Container, Button, Input, Segment, Grid, Divider, Header 
} from 'semantic-ui-react'
import PropTypes from 'prop-types'

import { setErrorMessage, setNotificationMessage } from 'store/actions/notification-actions'
import { updateScore } from 'store/actions/word-actions'
import { useField } from 'utils/useField'


const getRandomValue = (id) => 
  Math.floor(Math.random() * id)

const getRandomizedWords = (words) =>
  words.sort((a, b) => getRandomValue(a.id) - getRandomValue(b.id))

export const TrainingWindow = ({ words, setWords }) => {
  const [current, setCurrent] = useState(0)
  const [score, setScore] = useState(0)
  const [changes, setChanges] = useState([])
  const [answer, resetAnswer] = useField('text')
  const [shuffled, setShuffled] = useState(getRandomizedWords([...words]))
  
  const user = useSelector(state => state.loggedUser)
  
  const dispatch = useDispatch()

  const handleKeyPress = (e) => {
    if (e.charCode === 13) {
      e.preventDefault()
      checkAnswer()
    }
  }

  const checkAnswer = () => {
    const correctAnswer = shuffled[current].translation.toLowerCase()
    
    if (correctAnswer === answer.value.toLowerCase()) {
      dispatch(
        setNotificationMessage(
          `Correct! Your score: ${score+1} / ${current+1}`
        )
      )
      
      setScore(score + 1)
      setChanges(changes.concat({wordId: shuffled[current].id, score:1, tries:1}))
    } else {
      dispatch(
        setErrorMessage(
          `Wrong. Correct answer: ${shuffled[current].translation}`
        )
      )
      
      setChanges(
        changes.concat({wordId: shuffled[current].id, score:0, tries:1})
      )
    }
    resetAnswer()
    setCurrent(current + 1)
  }

  const finishRound = () => {
    setShuffled([])
    dispatch(updateScore(user, changes))
    setWords([])
  }

  if (current >= shuffled.length) {
    return (
      <Container>
        <Header>Your score: {score} / {words.length}</Header>
        <Button positive onClick={finishRound}>Finish session</Button>
      </Container>
    )
  }

  return (
    <Container>
      <Segment placeholder>
        <Grid columns={2} stackable textAlign='center'>
          <Divider vertical />

          <Grid.Row>
            <Grid.Column>
              <Header as='h1'>
                {shuffled[current].word}
              </Header>
            </Grid.Column>

            <Grid.Column>
              <Input 
                onKeyPress={handleKeyPress}
                fluid
                {...answer}
                placeholder='Write the translation...' />
            </Grid.Column>
          </Grid.Row>
          
          <Grid.Row>
            <Grid.Column>
              <Header disabled>
                {shuffled[current].lang_word}
              </Header>
            </Grid.Column>
            
            <Grid.Column>
              <Header disabled>
                {shuffled[current].lang_translation}
              </Header>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Segment>
    </Container>
  )
}

TrainingWindow.propTypes = {
  words: PropTypes.array.isRequired,
  setWords: PropTypes.func.isRequired
}