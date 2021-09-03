import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { 
  Table, Dropdown, Button, Input, Segment
} from 'semantic-ui-react'

import { setErrorMessage } from 'store/actions/notification-actions'
import { wordService } from 'services/word-service'
import { useField } from 'utils/useField'
import { languageOptions } from '../WordList'
import { TrainingWindow } from './TrainingWindow'


export const TrainingView = () => {
  const [words, setWords] = useState([])
  const [wordLang, resetWordLang] = useField('dropdown')
  const [transLang, resetTransLang] = useField('dropdown')
  const [size, setSize] = useState(0)
  const user = useSelector(state => state.loggedUser)
  const dispatch = useDispatch()
  
  const getTrainingWords = async () => {
    if (size > 0 && size < 30) {
      const response = await wordService.getTrainWords(
        user, wordLang.value, transLang.value, size
      )
      const words = response.data

      if (words.length === 0)
        dispatch(setErrorMessage('No words were found.'))
      else
        setWords(words)
      setSize(0)
      resetWordLang()
      resetTransLang()
    } else {
      dispatch(setErrorMessage('Size must be between 0 and 30'))
    }
  }

  const updateSize = (e) => {
    if (e.target.value !== '' && !isNaN(e.target.value)) {
      const number = Number(e.target.value)
      if (number) {
        if (number < 1 && number >= 30)
          dispatch(setErrorMessage('Size must be between 0 and 30'))
        else
          setSize(number)
      }
      else
        dispatch(setErrorMessage('Entered size is not valid'))
    }
  }

  return (
    <Segment>
      { words.length === 0 &&
        <Table>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell>
                Translation language
              </Table.HeaderCell>
              <Table.HeaderCell>
                Training language
              </Table.HeaderCell>
              <Table.HeaderCell>
                Number of words
              </Table.HeaderCell>
              <Table.HeaderCell />
            </Table.Row>

            <Table.Row>
              <Table.HeaderCell>
                <Dropdown fluid {...wordLang} placeholder='Select Language' selection options={languageOptions} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Dropdown fluid {...transLang} placeholder='Select Language' selection options={languageOptions} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Input fluid placeholder='Select size' onChange={updateSize} />
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Button positive onClick={getTrainingWords}>Start</Button>
              </Table.HeaderCell>
            </Table.Row>
          </Table.Header>
        </Table>
      }
      { words.length > 0 && <TrainingWindow words={words} setWords={setWords} /> }
    </Segment>
  )
}
