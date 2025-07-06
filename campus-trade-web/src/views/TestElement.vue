<template>
  <div style="padding: 20px;">
    <h2>Element Plus 组件测试</h2>
    
    <h3>Radio Group 测试</h3>
    <el-radio-group v-model="testValue">
      <el-radio-button label="option1">选项1</el-radio-button>
      <el-radio-button label="option2">选项2</el-radio-button>
      <el-radio-button label="option3">选项3</el-radio-button>
    </el-radio-group>
    
    <p>当前选中值: {{ testValue }}</p>
    
    <h3>Button 测试</h3>
    <el-button type="primary">主要按钮</el-button>
    <el-button type="success">成功按钮</el-button>
    <el-button type="warning">警告按钮</el-button>
    
    <h3>Input 测试</h3>
    <el-input v-model="inputValue" placeholder="请输入内容"></el-input>
    <p>输入值: {{ inputValue }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const testValue = ref('option1');
const inputValue = ref('');
</script> 