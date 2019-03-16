import React from 'react'
import styled from 'styled-components'

const Menu = styled.div`
  display: flex;
  opacity: 0.7;
  height: 25px;
  background-color: #4e4e4e;
  margin: 0 auto;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
  max-width: 1000px;
  box-shadow: 0 19px 38px rgba(0, 0, 0, 0.3), 0 15px 12px rgba(0, 0, 0, 0.22);
`

const FakeButton = styled.div`
  height: 10px;
  width: 10px;
  border-radius: 50%;
  border: 1px solid #000;
  position: relative;
  top: 6px;
  left: 6px;
  background-color: #ff3b47;
  border-color: #9d252b;
  display: inline-block;
`

const FakeMinimize = styled(FakeButton)`
  left: 11px;
  background-color: #ffc100;
  border-color: #9d802c;
`

const FakeZoom = styled(FakeButton)`
  left: 16px;
  background-color: #00d742;
  border-color: #049931;
`

export const TabBar = () => (
  <Menu>
    <FakeButton />
    <FakeMinimize />
    <FakeZoom />
  </Menu>
)
